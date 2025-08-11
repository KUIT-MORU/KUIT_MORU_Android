package com.konkuk.moru.data.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class RoutineDetailResponse(
    val id: String,
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val likeCount: Int = 0,
    val createdAt: String = "",
    val requiredTime: String = "",
    val isRunning: Boolean = false,
    val scheduledDays: List<String> = emptyList(),
    val scheduledTime: String? = null,
    val steps: List<RoutineStepResponse> = emptyList(),
    val author: AuthorResponse? = null,
    val authorId: String? = null,
    val authorName: String? = null,
    val authorEmail: String? = null,
    val authorProfileImageUrl: String? = null
)

@Serializable
@JsonIgnoreUnknownKeys
data class AuthorResponse(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val profileImageUrl: String? = null
) 