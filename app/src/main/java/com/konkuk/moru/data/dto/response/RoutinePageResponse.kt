package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RoutinePageResponse(
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val number: Int = 0,
    val size: Int = 0,
    val content: List<RoutineResponse> = emptyList()
)
