package com.konkuk.moru.presentation.routinefeed.screen.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.presentation.routinefeed.data.RoutineInfo
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
        val previousState = _uiState.value // 롤백을 위해 현재 상태 저장
        val isNowFollowing = !previousState.isFollowing

        // 1. UI 즉시 업데이트
        _uiState.update { currentState ->
            currentState.copy(
                isFollowing = isNowFollowing,
                // 팔로우하면 상대방의 팔로워 수 +1, 언팔로우하면 -1
                followerCount = if (isNowFollowing) {
                    currentState.followerCount + 1
                } else {
                    currentState.followerCount - 1
                }
            )
        }

// 2. 서버 API 요청 (실패 시 롤백 로직 포함)
        viewModelScope.launch {
            try {
                // TODO: 실제 서버 API를 호출하는 로직을 여기에 구현합니다.
                println("서버 API 요청: 팔로우 상태 -> $isNowFollowing")
                // 만약 서버 요청이 실패했다면 아래 catch 블록으로 빠집니다.
                // 성공 시에는 아무것도 할 필요 없습니다. UI는 이미 갱신되었으니까요.
            } catch (e: Exception) {
                // API 요청 실패! UI를 이전 상태로 되돌립니다.
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