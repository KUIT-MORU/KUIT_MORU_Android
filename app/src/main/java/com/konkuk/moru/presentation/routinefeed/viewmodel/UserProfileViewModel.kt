package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.DummyData
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        // 내비게이션 경로로부터 'userId' 파라미터를 가져옵니다.
        // 이 "userId"는 NavHost에 정의된 `navArgument("userId")`의 이름과 일치해야 합니다.
        val userId: Int? = savedStateHandle["userId"]

        if (userId != null) {
            loadUserProfile(userId)
        } else {
            // userId가 없는 비정상적인 경우, 기본 프로필이나 에러 상태를 표시합니다.
            loadDefaultProfile()
        }
    }

    /**
     * 전달받은 userId에 맞는 사용자 정보를 불러와 UI 상태를 업데이트합니다.
     */
    private fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            // 더미 데이터에서 userId와 일치하는 사용자 정보를 찾습니다.
            val user = DummyData.dummyUsers.find { it.userId == userId }
            // 더미 데이터에서 authorId가 일치하는 루틴 목록을 찾습니다.
            val userRoutines = DummyData.feedRoutines.filter { it.authorId == userId }
            val followerCount = DummyData.dummyFollowRelations.count { it.followingId == userId }
            val followingCount = DummyData.dummyFollowRelations.count { it.followerId == userId }

            val isFollowing = DummyData.dummyFollowRelations.any {
                it.followerId == DummyData.MY_USER_ID && it.followingId == userId
            }

            if (user != null) {
                // 찾은 사용자 정보로 UI 상태를 업데이트합니다.
                _uiState.update {
                    it.copy(
                        userId = userId,
                        profileImageUrl = user.profileImageUrl,
                        nickname = user.nickname,
                        bio = user.bio + " (ID: ${user.userId})", // 실제 앱에서는 user 모델에 bio 필드가 있어야 합니다.
                        routineCount = userRoutines.size,
                        followerCount = followerCount,
                        followingCount = followingCount,
                        isFollowing = isFollowing, // 기본 상태는 false로 설정
                        runningRoutines = userRoutines.filter { routine -> routine.isRunning },
                        userRoutines = userRoutines.filter { routine -> !routine.isRunning }
                    )
                }
            } else {
                // 해당하는 유저 정보가 없으면 기본 프로필을 로드합니다.
                loadDefaultProfile()
            }
        }
    }

    /**
     * userId를 찾지 못했거나 없는 경우 호출되는 기본 상태 로드 함수입니다.
     */
    private fun loadDefaultProfile() {
        _uiState.update {
            it.copy(
                nickname = "알 수 없는 사용자",
                bio = "사용자 정보를 불러오는 데 실패했습니다.",
                routineCount = 0,
                followerCount = 0,
                followingCount = 0,
                runningRoutines = emptyList(),
                userRoutines = emptyList()
            )
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
                if (routine.routineId == routineId) {
                    routine.copy(
                        isLiked = !routine.isLiked,
                        likes = if (!routine.isLiked) routine.likes + 1 else routine.likes - 1
                    )
                } else {
                    routine
                }
            }
            val updatedUserRoutines = currentState.userRoutines.map { routine ->
                if (routine.routineId == routineId) {
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