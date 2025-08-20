package com.konkuk.moru.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateRoutineRequest(
    @SerializedName("title") val title: String,
    @SerializedName("imageKey") val imageKey: String?,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("description") val description: String,
    @SerializedName("steps") val steps: List<StepDto>?,      // ← List<StepDto>? 로 변경 (nullable)
    @SerializedName("selectedApps") val selectedApps: List<String>?, // 이미 nullable OK
    @SerializedName("isSimple") val isSimple: Boolean,
    @SerializedName("isUserVisible") val isUserVisible: Boolean
)