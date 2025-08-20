package com.konkuk.moru.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateRoutineRequest(
    @SerializedName("title") val title: String,
    @SerializedName("imageKey") val imageKey: String?,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("description") val description: String,
    @SerializedName("steps") val steps: List<StepDto>,
    @SerializedName("selectedApps") val selectedApps: List<String>,
    @SerializedName("isSimple") val isSimple: Boolean,
    @SerializedName("isUserVisible") val isUserVisible: Boolean
)

data class StepDto(
    @SerializedName("name") val name: String,
    @SerializedName("stepOrder") val stepOrder: Int,
    // ISO-8601 duration string (e.g., "PT5M", "PT30M")
    @SerializedName("estimatedTime") val estimatedTime: String
)
