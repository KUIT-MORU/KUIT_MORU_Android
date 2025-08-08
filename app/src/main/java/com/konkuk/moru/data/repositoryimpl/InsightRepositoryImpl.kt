package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.service.InsightService
import com.konkuk.moru.domain.repository.InsightRepository
import com.konkuk.moru.data.dto.response.InsightResponseDto
import javax.inject.Inject

class InsightRepositoryImpl @Inject constructor(
    private val service: InsightService
) : InsightRepository {

    override suspend fun getInsights(): Result<InsightResponseDto> {
        return try {
            Result.success(service.getInsights())
        } catch (e: Exception) {
            Log.e("InsightRepository", "getInsights() 실패", e)
            Result.failure(e)
        }
    }
}