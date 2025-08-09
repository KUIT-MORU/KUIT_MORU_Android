package com.konkuk.moru.domain.repository

import com.konkuk.moru.data.dto.response.InsightResponseDto

interface InsightRepository {
    suspend fun getInsights(): Result<InsightResponseDto>
}