package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.request.LoginRequestDto
import com.konkuk.moru.data.dto.response.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): LoginResponseDto
}