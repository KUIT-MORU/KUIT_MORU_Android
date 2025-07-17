package com.konkuk.moru.presentation.routinefeed.screen.follow


import com.konkuk.moru.presentation.routinefeed.data.FollowUser

data class FollowUiState(
    val followers: List<FollowUser> = emptyList(),
    val followings: List<FollowUser> = emptyList()
    // val isLoading: Boolean = true // 데이터 로딩 중 상태 표시도 가능
)

data class FollowRelation(
    val followerId: Int, // 팔로우를 하는 사람 ID
    val followingId: Int  // 팔로우를 당하는 사람 ID
)