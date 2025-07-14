package com.konkuk.moru.presentation.routinefeed.screen.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// [수정] 통합 Routine 모델과 UiState를 임포트합니다.
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.routinefeed.data.UserProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // [수정] 가상 데이터를 통합 Routine 모델로 변경합니다.
            _uiState.update {
                it.copy(
                    nickname = "팔로우",
                    bio = "자기소개입니다. 자기소개입니다.",
                    routineCount = 4,
                    followerCount = 628,
                    followingCount = 221,
                    isFollowing = false,
                    runningRoutines = listOf(
                        Routine(1, "아침 운동 1", "", null, "운동", listOf("#테그그그그그", "#tag"), "모루", null, 16, true, false, true)
                    ),
                    userRoutines = emptyList() // 필요 시 여기도 채워주세요.
                )
            }
        }
    }

    fun toggleFollow() {
        val previousState = _uiState.value
        val isNowFollowing = !previousState.isFollowing

        _uiState.update { currentState ->
            currentState.copy(
                isFollowing = isNowFollowing,
                followerCount = if (isNowFollowing) {
                    currentState.followerCount + 1
                } else {
                    currentState.followerCount - 1
                }
            )
        }

        viewModelScope.launch {
            try {
                // TODO: 실제 서버 API 호출
                println("서버 API 요청: 팔로우 상태 -> $isNowFollowing")
            } catch (e: Exception) {
                _uiState.value = previousState
                println("서버 API 요청 실패! UI를 원래대로 롤백합니다.")
            }
        }
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