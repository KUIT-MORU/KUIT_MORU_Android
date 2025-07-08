package com.konkuk.moru.presentation.routinefeed.screen.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.presentation.routinefeed.data.RoutineInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserProfileUiState(
    val nickname: String = "",
    val bio: String = "",
    val routineCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val runningRoutines: List<RoutineInfo> = emptyList(),
    val userRoutines: List<RoutineInfo> = emptyList(),
    val isRunningRoutineExpanded: Boolean = true
)

class UserProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // 가상 데이터 (서버 연동 시 교체)
            _uiState.update {
                it.copy(
                    nickname = "팔로우",
                    bio = "자기소개입니다. 자기소개입니다.",
                    routineCount = 4,
                    followerCount = 628,
                    followingCount = 221,
                    isFollowing = false,
                    runningRoutines = listOf(
                        RoutineInfo(1, "아침 운동 1", listOf("#테그그그그그", "#tag"), 16, true, false)
                    ),
                    userRoutines = emptyList()
                )
            }
        }
    }

    fun toggleFollow() {
        _uiState.update { it.copy(isFollowing = !it.isFollowing) }
        // TODO: 서버에 팔로우/언팔로우 API 요청
    }

    fun toggleRunningRoutineExpansion() {
        _uiState.update { it.copy(isRunningRoutineExpanded = !it.isRunningRoutineExpanded) }
    }

    fun toggleLike(routineId: Int) {
        _uiState.update { currentState ->
            val updatedRunningRoutines = currentState.runningRoutines.map { routine ->
                if (routine.id == routineId) {
                    routine.copy(
                        isLiked = !routine.isLiked,
                        likes = if (!routine.isLiked) routine.likes + 1 else routine.likes - 1
                    )
                } else {
                    routine
                }
            }
            val updatedUserRoutines = currentState.userRoutines.map { routine ->
                if (routine.id == routineId) {
                    routine.copy(
                        isLiked = !routine.isLiked,
                        likes = if (!routine.isLiked) routine.likes + 1 else routine.likes - 1
                    )
                } else {
                    routine
                }
            }
            currentState.copy(
                runningRoutines = updatedRunningRoutines,
                userRoutines = updatedUserRoutines
            )
        }
        // TODO: 서버에 좋아요 상태 변경 API 요청
    }
}