package com.konkuk.moru.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoutineResponse(
    @SerialName("id")
    val routineId: String,
    val title: String,
    val imageUrl: String? = null,
    val category: String? = null,
    val tags: List<String>,
    val likeCount: Int,
    val createdAt: String,
    val requiredTime: String,
    val isRunning: Boolean,
    val scheduledDays: List<String> = emptyList(),  // 요일별 스케줄 (MON, TUE, WED, ...)
    val scheduledTime: String? = null               // 스케줄 시간 (HH:mm)
)
