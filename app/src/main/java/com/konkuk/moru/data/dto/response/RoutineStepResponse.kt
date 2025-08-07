package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RoutineStepResponse(
    val name: String,
    val duration: String  // "MM:SS"
)
