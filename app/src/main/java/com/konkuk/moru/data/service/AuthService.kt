package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.request.LoginRequest
import com.konkuk.moru.data.dto.response.LoginResponse
import com.konkuk.moru.data.dto.response.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("/api/user/me")
    suspend fun getUserProfile(): UserProfileResponse
}