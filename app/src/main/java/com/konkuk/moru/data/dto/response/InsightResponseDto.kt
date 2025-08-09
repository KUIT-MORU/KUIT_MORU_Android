package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class InsightResponseDto(
    val paceGrade: String,
    val routineCompletionRate: Int,
    val globalAverageRoutineCompletionRate: Int,
    val completionDistribution: CompletionDistributionDto,
    val averageRoutineCount: AverageRoutineCountDto,
    val routineCompletionCountByTimeSlot: CompletionByTimeSlotDto
)

@Serializable
data class CompletionDistributionDto(
    val additionalProp1: Int,
    val additionalProp2: Int,
    val additionalProp3: Int
)

@Serializable
data class AverageRoutineCountDto(
    val weekday: UserOverallCountDto,
    val weekend: UserOverallCountDto
)

@Serializable
data class UserOverallCountDto(
    val user: Int,
    val overall: Int
)

@Serializable
data class CompletionByTimeSlotDto(
    val additionalProp1: Int,
    val additionalProp2: Int,
    val additionalProp3: Int
)
