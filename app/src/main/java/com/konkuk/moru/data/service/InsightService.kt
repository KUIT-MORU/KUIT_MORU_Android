package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.response.InsightResponseDto
import retrofit2.http.GET

interface InsightService {
    @GET("/api/insights/")
    suspend fun getInsights(): InsightResponseDto
}