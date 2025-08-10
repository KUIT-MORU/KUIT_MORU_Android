package com.konkuk.moru.data.repositoryimpl

// 얻은 사용자 정보를 적용하기 위한 파일
import android.content.Context
import com.konkuk.moru.core.datastore.TokenPreference
import com.konkuk.moru.data.dto.request.LoginRequestDto
import com.konkuk.moru.data.dto.response.LoginResponseDto
import com.konkuk.moru.data.dto.response.UserProfileResponse
import com.konkuk.moru.data.service.AuthService
import javax.inject.Inject

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
    ): LoginResponseDto {
        val response = service.login(LoginRequestDto(email, password))
        TokenPreference.setTokens(
            context,
            response.token.accessToken,
            response.token.refreshToken
        )
        return response
    }
}
