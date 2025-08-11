package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.RoutineSyncBus
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
import javax.inject.Inject

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
    // [추가] 페이징 커서 보관
    private var followersCursor: FollowCursorDto? = null
    private var followingsCursor: FollowCursorDto? = null
    private var ownerUserId: String = savedStateHandle["userId"] ?: ""

    init {
        // 첫 페이지 로드
        loadFollowers(refresh = true)
        loadFollowings(refresh = true)
    }

    fun loadFollowers(refresh: Boolean = false) {
        viewModelScope.launch {
            val lastNick = if (refresh) null else followersCursor?.nickname
            val lastId = if (refresh) null else followersCursor?.userId
            runCatching {
                socialRepository.getFollowers(ownerUserId, lastNick, lastId, limit = 10)
            }.onSuccess { res ->
                val items = res.content.map { it.toUi() }
                _uiState.update {
                    it.copy(
                        followers = if (refresh) items else it.followers + items
                    )
                }
                followersCursor = res.nextCursor
            }
        }
    }

    fun loadFollowings(refresh: Boolean = false) {
        viewModelScope.launch {
            val lastNick = if (refresh) null else followingsCursor?.nickname
            val lastId = if (refresh) null else followingsCursor?.userId
            runCatching {
                socialRepository.getFollowing(ownerUserId, lastNick, lastId, limit = 10)
            }.onSuccess { res ->
                val meId = runCatching { userRepository.getMe().id }.getOrNull()
                val mapped = res.content.map { it.toUi() }
                val items = if (meId != null && ownerUserId == meId) {
                    mapped.map { it.copy(isFollowing = true) }   // ✅ 강제 ON
                } else mapped

                _uiState.update { it.copy(followings = if (refresh) items else it.followings + items) }
                followingsCursor = res.nextCursor
            }
        }
    }

    fun toggleFollow(clickedUser: FollowUser) {
        val before = _uiState.value
        val wantFollow = !clickedUser.isFollowing

        // 1) 낙관적 업데이트
        _uiState.update { current ->
            current.copy(
                followers = current.followers.map {
                    if (it.id == clickedUser.id) it.copy(isFollowing = wantFollow) else it
                },
                followings = if (wantFollow) {
                    // 새로 팔로우 → 팔로잉 목록에 없으면 앞에 추가
                    if (current.followings.any { it.id == clickedUser.id }) current.followings
                    else listOf(clickedUser.copy(isFollowing = true)) + current.followings
                } else {
                    // 언팔 → 팔로잉 목록에서 제거
                    current.followings.filterNot { it.id == clickedUser.id }
                }
            )
        }

        // 2) 서버 반영
        viewModelScope.launch {
            runCatching {
                if (wantFollow) socialRepository.follow(clickedUser.id)
                else socialRepository.unfollow(clickedUser.id)
            }.onSuccess {
                _followResult.tryEmit(clickedUser.id to wantFollow)

                // [추가] 전역 이벤트 발행 → 프로필/피드 동기화
                RoutineSyncBus.publish(
                    RoutineSyncBus.Event.Follow(
                        userId = clickedUser.id,
                        isFollowing = wantFollow
                    )
                )
            }.onFailure {
                _uiState.value = before
            }
        }
    }
}