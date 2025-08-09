package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val token: TokenDto,
    val isOnboarding: Boolean
)
