package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.request.LoginRequestDto
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Pair<String, String>> {
        return try {
            val response = service.login(LoginRequestDto(email, password))
            Result.success(response.accessToken to response.refreshToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}