package com.konkuk.moru.data.repositoryimpl

import android.content.Context
import com.konkuk.moru.core.datastore.TokenPreference
import com.konkuk.moru.data.dto.request.LoginRequest
import com.konkuk.moru.data.dto.response.LoginResponse
import com.konkuk.moru.data.dto.response.UserProfileResponse
import com.konkuk.moru.data.service.AuthService
import javax.inject.Inject

// 얻은 사용자 정보를 적용하기 위한 파일
class AuthRepository @Inject constructor(
    private val service: AuthService
) {
    suspend fun getUserProfile(): UserProfileResponse {
        return service.getUserProfile()
    }

    suspend fun loginAndSaveTokens(
        context: Context,
        email: String,
        password: String
    ): LoginResponse {
        val response = service.login(LoginRequest(email, password))
        TokenPreference.setTokens(
            context,
            response.token.accessToken,
            response.token.refreshToken
        )
        return response
    }

}