package com.konkuk.moru.presentation.routinefeed.data

data class FollowUser(
    val id: Int,
    val profileImageUrl: String, // 임시로 비워두거나 기본 이미지 URL 사용
    val username: String,
    val bio: String,
    val isFollowing: Boolean
)