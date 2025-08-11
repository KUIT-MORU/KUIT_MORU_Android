package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.response.UserProfileResponse
import retrofit2.http.GET

interface UserService {
    @GET("/api/user/me")
    suspend fun getMe(): UserProfileResponse
}
