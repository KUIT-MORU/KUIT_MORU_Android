package com.konkuk.moru.presentation.routinefeed.data

data class FollowUser(
    val id: Int,
    val profileImageUrl: String, // 임시로 비워두거나 기본 이미지 URL 사용
    val username: String,
    val bio: String,
    val isFollowing: Boolean
)


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