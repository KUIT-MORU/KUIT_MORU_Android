package com.konkuk.moru.presentation.routinefeed.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineCardDomain
import com.konkuk.moru.domain.repository.SocialRepository
import com.konkuk.moru.domain.repository.UserRepository
import com.konkuk.moru.presentation.routinefeed.data.UserProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        val userId: String? = savedStateHandle["userId"]
        viewModelScope.launch {
            val domain = runCatching {
                val argUserId: String? = savedStateHandle["userId"]
                if (argUserId.isNullOrBlank()) {
                    val me = userRepository.getMe()
                    userRepository.getUserProfile(me.id)
                } else {
                    userRepository.getUserProfile(argUserId)
                }
            }.getOrElse { e ->
                _uiState.update {
                    it.copy(
                        nickname = "알 수 없는 사용자",
                        bio = "사용자 정보를 불러오는 데 실패했습니다. (${e.message ?: "알 수 없는 오류"})",
                        routineCount = 0,
                        followerCount = 0,
                        followingCount = 0,
                        runningRoutines = emptyList(),
                        userRoutines = emptyList()
                    )
                }
                return@launch
            }

            _uiState.update { prev ->
                prev.copy(
                    userId = domain.id,
                    isMe = domain.isMe,
                    profileImageUrl = domain.profileImageUrl,
                    nickname = domain.nickname,
                    bio = domain.bio ?: "",
                    routineCount = domain.routineCount,
                    followerCount = domain.followerCount,
                    followingCount = domain.followingCount,
                    isFollowing = false, // 일단 false로 두고…
                    runningRoutines = domain.currentRoutine?.let {
                        listOf(it.toUiRoutine(domain.id, domain.nickname, domain.profileImageUrl))
                    } ?: emptyList(),
                    userRoutines = domain.routines.map {
                        it.toUiRoutine(domain.id, domain.nickname, domain.profileImageUrl)
                    }
                )
            }

            // ✅ 여기서 실제 초기 팔로잉 여부를 보정
            refreshInitialFollowing()
        }
    }

    private suspend fun refreshInitialFollowing() {
        val targetId = _uiState.value.userId ?: return
        // 내 프로필이면 버튼 자체를 숨기거나 비활성 처리
        if (_uiState.value.isMe == true) {
            _uiState.update { it.copy(isFollowing = false) }
            return
        }

        // 내 아이디 조회
        val meId = runCatching { userRepository.getMe().id }.getOrNull().orEmpty()
        if (meId.isBlank()) return

        // ✅ 내 팔로잉 1페이지(혹은 넉넉히) 조회 후 포함 여부 확인
        val page = runCatching {
            socialRepository.getFollowing(
                userId = meId,
                lastNickname = null,
                lastUserId = null,
                limit = 200 // 숫자 넉넉히
            )
        }.getOrNull()

        val found = page?.content?.any { it.userId == targetId } == true
        _uiState.update { it.copy(isFollowing = found) }
    }



    fun toggleFollow() {
        val before = _uiState.value

        // [추가] 유효성 체크: 대상 유저 ID가 비어있거나, 내 프로필이면 리턴
        val targetUserId: String = before.userId ?: return

        if (targetUserId.isBlank() || before.isMe == true) return

        // [추가] 중복 탭 방지
        if (before.isFollowLoading) return

        val wantFollow = !before.isFollowing

        // 1) 낙관적 업데이트 + 로딩 시작
        _uiState.update {
            it.copy(
                isFollowLoading = true, // [추가]
                isFollowing = wantFollow,
                followerCount = (it.followerCount + if (wantFollow) 1 else -1).coerceAtLeast(0)
            )
        }

        // 2) 서버 반영
        viewModelScope.launch {
            runCatching {
                if (wantFollow) socialRepository.follow(targetUserId)
                else socialRepository.unfollow(targetUserId)
            }.onSuccess {
                // [추가] 로딩 종료 (성공)
                _uiState.update { it.copy(isFollowLoading = false) }
            }.onFailure {
                // 3) 실패 시 롤백 + 로딩 종료
                _uiState.value = before.copy(isFollowLoading = false) // [변경]
            }
        }
    }

    fun toggleRunningRoutineExpansion() {
        _uiState.update { it.copy(isRunningRoutineExpanded = !it.isRunningRoutineExpanded) }
    }

    fun toggleLike(routineId: String) {
        _uiState.update { current ->
            val updatedRunning = current.runningRoutines.map { r ->
                if (r.routineId == routineId) r.copy(
                    isLiked = !r.isLiked,
                    likes = if (!r.isLiked) r.likes + 1 else r.likes - 1
                ) else r
            }
            val updatedUser = current.userRoutines.map { r ->
                if (r.routineId == routineId) r.copy(
                    isLiked = !r.isLiked,
                    likes = if (!r.isLiked) r.likes + 1 else r.likes - 1
                ) else r
            }
            current.copy(runningRoutines = updatedRunning, userRoutines = updatedUser)
        }
        // TODO: 서버 좋아요 토글 API 호출
    }

    fun applyExternalFollow(isFollowing: Boolean) {
        _uiState.update { s ->
            val delta = when {
                isFollowing && !s.isFollowing -> +1
                !isFollowing && s.isFollowing -> -1
                else -> 0
            }
            s.copy(
                isFollowing = isFollowing,
                followerCount = (s.followerCount + delta).coerceAtLeast(0)
            )
        }
    }


}

/**
 * Domain → UI 모델 매핑
 * UI에서 사용하는 Routine(피드 카드용)으로 가볍게 채워 넣습니다.
 * authorId는 프로필 주인 id로 세팅(상세로 넘어갈 때 작성자 프로필 이동 등에 사용 가능)
 */
private fun RoutineCardDomain.toUiRoutine(
    profileOwnerId: String,
    authorName: String,
    authorProfileUrl: String?
): Routine =
    Routine(
        routineId = id,
        title = title,
        imageUrl = imageUrl,
        tags = tags,
        likes = likeCount,
        isRunning = isRunning,

        // UI에서 필요하지만 서버 카드 응답에 없는 값들은 기본값으로
        description = "",
        category = "일상",
        authorId = profileOwnerId ?: "", // 프로필 화면 주인의 id
        authorName = authorName,
        authorProfileUrl = authorProfileUrl,
        isLiked = false,
        isBookmarked = false,

        isChecked = false,
        scheduledTime = null,
        scheduledDays = emptySet(),
        isAlarmEnabled = false,
        steps = emptyList(),
        similarRoutines = emptyList(),
        usedApps = emptyList()
    )