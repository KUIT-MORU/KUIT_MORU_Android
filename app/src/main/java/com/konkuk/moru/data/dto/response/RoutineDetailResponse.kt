package com.konkuk.moru.data.dto.response

import com.google.gson.annotations.SerializedName
import com.konkuk.moru.presentation.routinefeed.data.AppDto
import com.konkuk.moru.presentation.routinefeed.data.AuthorDto
import com.konkuk.moru.presentation.routinefeed.data.RoutineStepDto
import com.konkuk.moru.presentation.routinefeed.data.SimilarRoutineItemDto

data class RoutineDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("author") val author: AuthorDto?,
    @SerializedName("tags") val tags: List<String>?=null,
    @SerializedName("description") val description: String,
    @SerializedName("isSimple") val isSimple: Boolean,
    @SerializedName("isUserVisible") val isUserVisible: Boolean,
    @SerializedName("steps") val steps: List<RoutineStepDto>?=null,
    @SerializedName("apps") val apps: List<AppDto>?=null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("requiredTime") val requiredTime: String?=null, // ISO-8601 Duration (e.g., PT50M)
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("isLiked") val isLiked: Boolean? = null,
    @SerializedName("scrapCount") val scrapCount: Int,
    @SerializedName("isScrapped") val isScrapped: Boolean? = null,
    @SerializedName("isOwner") val isOwner: Boolean,
    @SerializedName("similarRoutines") val similarRoutines: List<SimilarRoutineItemDto>?=null,
)