package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RoutineDetailResponse(
    val id: String,
    val title: String,
    val imageUrl: String? = null,
    val tags: List<String>,
    val likeCount: Int,
    val createdAt: String,
    val requiredTime: String,
    val isRunning: Boolean,
    val scheduledDays: List<String> = emptyList(),
    val scheduledTime: String? = null,
    val steps: List<RoutineStepResponse> = emptyList(),
    val description: String? = null,
    val category: String? = null
) 