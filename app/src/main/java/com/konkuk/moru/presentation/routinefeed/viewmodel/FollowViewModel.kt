package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.RoutineSyncBus
import com.konkuk.moru.core.datastore.SocialMemory
import com.konkuk.moru.data.dto.response.FollowCursorDto
import com.konkuk.moru.data.mapper.toUi
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.domain.repository.SocialRepository
import com.konkuk.moru.domain.repository.UserRepository
import com.konkuk.moru.presentation.routinefeed.data.FollowUser
import com.konkuk.moru.presentation.routinefeed.screen.follow.FollowUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource



@HiltViewModel
class FollowViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowUiState())
    val uiState = _uiState.asStateFlow()

    private val _followResult = MutableSharedFlow<Pair<String, Boolean>>(extraBufferCapacity = 1)
    val followResult = _followResult.asSharedFlow()

    // 페이징 커서
    private var followersCursor: FollowCursorDto? = null
    private var followingsCursor: FollowCursorDto? = null

    private val ownerUserId: String = savedStateHandle["userId"] ?: ""

    // 비동기로 채우는 내 아이디 캐시
    private var myId: String? = null

    // === 낙관 팔로우 TTL 가드 ===
    private data class FollowStamp(val wantFollow: Boolean, val mark: TimeMark = TimeSource.Monotonic.markNow())
    private val followStampMap = ConcurrentHashMap<String, FollowStamp>()
    private val PROTECT_TTL = 2.seconds

    /** 서버값 받았을 때, TTL 내 토글 대상이면 낙관값을 우선시 */
    private fun guardFollow(userId: String, serverIsFollowing: Boolean): Boolean {
        val s = followStampMap[userId] ?: return serverIsFollowing
        return if (s.mark.elapsedNow() <= PROTECT_TTL) s.wantFollow else serverIsFollowing
    }

    init {
        // 내 아이디 먼저 확보 → UI에 전달
        viewModelScope.launch {
            ensureMeId()
            // 첫 페이지 로드
            loadFollowers(refresh = true)
            loadFollowings(refresh = true)
        }
    }

    /** 필요 시 한 번만 내 아이디를 조회해서 캐시 + uiState 반영 */
    private suspend fun ensureMeId(): String? {
        if (myId != null) return myId
        myId = runCatching { userRepository.getMe().id }.getOrNull()
        _uiState.update { it.copy(myId = myId) } // FollowUiState에 meId 필드가 있다고 가정
        return myId
    }

    fun loadFollowers(refresh: Boolean = false) {
        viewModelScope.launch {
            val lastNick = if (refresh) null else followersCursor?.nickname
            val lastId = if (refresh) null else followersCursor?.userId

            runCatching { socialRepository.getFollowers(ownerUserId, lastNick, lastId, limit = 10) }
                .onSuccess { res ->
                    val mapped = res.content.map { it.toUi() }
                    val items = mapped.map { it.copy(isFollowing = guardFollow(it.id, it.isFollowing)) }
                    _uiState.update { it.copy(followers = if (refresh) items else it.followers + items) }
                    followersCursor = res.nextCursor
                }
        }
    }

    fun loadFollowings(refresh: Boolean = false) {
        viewModelScope.launch {
            val lastNick = if (refresh) null else followingsCursor?.nickname
            val lastId = if (refresh) null else followingsCursor?.userId

            runCatching { socialRepository.getFollowing(ownerUserId, lastNick, lastId, limit = 10) }
                .onSuccess { res ->
                    val base = res.content.map { it.toUi() }
                    val me = ensureMeId()

                    // 내가 내 페이지를 볼 땐 팔로잉 목록의 isFollowing을 강제로 true
                    val forced = if (me != null && ownerUserId == me) {
                        base.map { it.copy(isFollowing = true) }
                    } else base

                    val items = forced.map { it.copy(isFollowing = guardFollow(it.id, it.isFollowing)) }
                    _uiState.update { it.copy(followings = if (refresh) items else it.followings + items) }
                    followingsCursor = res.nextCursor
                }
        }
    }

    fun toggleFollow(clickedUser: FollowUser) {
        val before = _uiState.value
        val wantFollow = !clickedUser.isFollowing

        // 낙관 UI
        _uiState.update { current ->
            current.copy(
                followers = current.followers.map {
                    if (it.id == clickedUser.id) it.copy(isFollowing = wantFollow) else it
                },
                followings = if (wantFollow) {
                    if (current.followings.any { it.id == clickedUser.id }) current.followings
                    else listOf(clickedUser.copy(isFollowing = true)) + current.followings
                } else {
                    current.followings.filterNot { it.id == clickedUser.id }
                }
            )
        }

        // 메모리 즉시 반영 + 스탬프
        SocialMemory.setFollow(clickedUser.id, wantFollow)
        followStampMap[clickedUser.id] = FollowStamp(wantFollow)

        viewModelScope.launch {
            runCatching {
                if (wantFollow) socialRepository.follow(clickedUser.id)
                else socialRepository.unfollow(clickedUser.id)
            }.onSuccess {
                _followResult.tryEmit(clickedUser.id to wantFollow)
                RoutineSyncBus.publish(
                    RoutineSyncBus.Event.Follow(
                        userId = clickedUser.id,
                        isFollowing = wantFollow
                    )
                )
                // 성공 후에도 TTL 동안 스탬프 유지 (서버 지연 덮어쓰기 방지)
            }.onFailure {
                // 롤백
                _uiState.value = before
                val rb = before.followers.find { it.id == clickedUser.id }?.isFollowing ?: false
                SocialMemory.setFollow(clickedUser.id, rb)
                followStampMap.remove(clickedUser.id)
            }
        }
    }
}