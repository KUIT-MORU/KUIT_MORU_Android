package com.konkuk.moru.data.service

import retrofit2.http.GET

// 서버 응답에 맞춰 필요한 필드만 사용
data class UserMeResponse(
    val id: String,
    val nickname: String
)

interface UserService {
    @GET("/api/user/me")
    suspend fun getMe(): UserMeResponse
}
