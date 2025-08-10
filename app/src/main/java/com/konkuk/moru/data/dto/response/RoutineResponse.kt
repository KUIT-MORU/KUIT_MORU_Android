package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RoutineResponse(
    val routineId: String,
    val title: String,
    val category: String,
    val tags: List<String>,
    val steps: List<RoutineStepResponse>
)
