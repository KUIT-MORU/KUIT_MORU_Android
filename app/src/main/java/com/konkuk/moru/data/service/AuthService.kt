package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.request.LoginRequestDto
import com.konkuk.moru.data.dto.request.RefreshRequestDto
import com.konkuk.moru.data.dto.response.LoginResponseDto
import com.konkuk.moru.data.dto.response.TokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body body: RefreshRequestDto): Response<TokenDto>
}