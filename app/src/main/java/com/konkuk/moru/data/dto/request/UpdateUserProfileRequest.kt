package com.konkuk.moru.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileRequest(
    val nickname: String,
    val gender: String,
    val birthday: String,
    val bio: String,
    val profileImageUrl: String?
)
