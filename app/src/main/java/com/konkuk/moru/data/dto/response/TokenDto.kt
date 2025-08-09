package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    val accessToken: String,
    val refreshToken: String
)