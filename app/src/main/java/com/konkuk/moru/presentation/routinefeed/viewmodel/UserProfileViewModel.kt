package com.konkuk.moru.presentation.routinefeed.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineCardDomain
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        // NavGraph의 route = "user_profile/{userId}" 와 인자 이름이 정확히 "userId" 인지 확인
        val userId: String? = savedStateHandle["userId"]
        viewModelScope.launch {
            runCatching {
                if (userId.isNullOrBlank()) {
                    // --- 👇 [핵심 수정 로직] 내 프로필 정보 로드 ---
                    // 1. /api/user/me API를 호출해 내 기본 정보와 ID를 얻어옵니다.
                    val myInfo = userRepository.getMe()
                    // 2. 위에서 얻은 내 ID를 사용해, 루틴 정보가 포함된
                    //    /api/user/{userId} API를 다시 호출합니다.
                    userRepository.getUserProfile(myInfo.id)
                    // --- [수정 로직 끝] ---
                } else {
                    // 타인 프로필
                    userRepository.getUserProfile(userId)
                }
            }.onSuccess { domain ->
                _uiState.update { prev ->
                    Log.d("MoruDebug", "State updating with nickname: ${domain.nickname}")
                    prev.copy(
                        userId = domain.id,
                        isMe = domain.isMe, // isMe 상태도 domain에서 가져옵니다.
                        profileImageUrl = domain.profileImageUrl,
                        nickname = domain.nickname,
                        bio = domain.bio ?: "",
                        routineCount = domain.routineCount,
                        followerCount = domain.followerCount,
                        followingCount = domain.followingCount,
                        isFollowing = false,
                        runningRoutines = domain.currentRoutine?.let {
                            listOf(it.toUiRoutine(domain.id, domain.nickname, domain.profileImageUrl))
                        } ?: emptyList(),
                        userRoutines = domain.routines.map {
                            it.toUiRoutine(domain.id, domain.nickname, domain.profileImageUrl)
                        }
                    )
                }
            }.onFailure { e ->
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
            }
        }
    }

    fun toggleFollow() {
        val previous = _uiState.value
        val nowFollowing = !previous.isFollowing
        _uiState.update {
            it.copy(
                isFollowing = nowFollowing,
                followerCount = if (nowFollowing) it.followerCount + 1 else it.followerCount - 1
            )
        }
        // TODO: 서버에 팔로우/언팔로우 요청 (실패 시 롤백)
        // 실패하면 _uiState.value = previous
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

        // UI에서 필요하지만 서버 카드 응답에 없는 값들은 기본값으로
        description = "",
        category = "일상",
        authorId = profileOwnerId ?: "", // 프로필 화면 주인의 id
        authorName = authorName,
        authorProfileUrl = authorProfileUrl,
        isLiked = false,
        isBookmarked = false,
        isRunning = false,
        isChecked = false,
        scheduledTime = null,
        scheduledDays = emptySet(),
        isAlarmEnabled = false,
        steps = emptyList(),
        similarRoutines = emptyList(),
        usedApps = emptyList()
    )