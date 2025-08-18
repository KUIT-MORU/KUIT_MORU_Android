package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.request.FcmTokenRequest
import com.konkuk.moru.data.service.FcmService
import com.konkuk.moru.domain.repository.FcmRepository
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val fcmService: FcmService
) : FcmRepository {
    override suspend fun registerFcmToken(token: String): Result<Unit> {
        return try {
            val response = fcmService.registerFcmToken(FcmTokenRequest(token))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable("FCM token registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}