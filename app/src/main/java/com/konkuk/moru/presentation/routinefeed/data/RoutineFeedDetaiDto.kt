package com.konkuk.moru.presentation.routinefeed.data

import com.google.gson.annotations.SerializedName

data class AuthorDto(
    @SerializedName("id") val id: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)

data class RoutineDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("author") val author: AuthorDto?,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("description") val description: String,
    @SerializedName("isSimple") val isSimple: Boolean,
    @SerializedName("isUserVisible") val isUserVisible: Boolean,
    @SerializedName("steps") val steps: List<RoutineStepDto>,
    @SerializedName("apps") val apps: List<AppDto>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("requiredTime") val requiredTime: String?, // ISO-8601 Duration (e.g., PT50M)
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("scrapCount") val scrapCount: Int,
    @SerializedName("isOwner") val isOwner: Boolean,
    @SerializedName("similarRoutines") val similarRoutines: List<SimilarRoutineItemDto>,


    // 선택: 서버가 내 상태도 내려주면(권장)
    @SerializedName("likedByMe") val likedByMe: Boolean? = null,
    @SerializedName("scrapedByMe") val scrapedByMe: Boolean? = null

)

data class RoutineStepDto(
    @SerializedName("id") val id: String,
    @SerializedName("stepOrder") val stepOrder: Int,
    @SerializedName("name") val name: String,
    @SerializedName("estimatedTime") val estimatedTime: String // ISO-8601 Duration (e.g., PT5M)
)

data class AppDto(
    @SerializedName("packageName") val packageName: String,
    @SerializedName("name") val name: String
)

data class SimilarRoutineItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("requiredTime") val requiredTime: String?
)