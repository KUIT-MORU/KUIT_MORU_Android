package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.dto.request.LoginRequestDto
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.data.token.TokenManager
import com.konkuk.moru.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Pair<String, String>> {
        return try {
            val res = service.login(LoginRequestDto(email, password))
            val access = res.token.accessToken
            val refresh = res.token.refreshToken

            Log.d("Login", "받은 accessToken = ${access.take(6)}...${access.takeLast(4)}")

            tokenManager.saveTokens(access, refresh)

            Result.success(access to refresh)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}