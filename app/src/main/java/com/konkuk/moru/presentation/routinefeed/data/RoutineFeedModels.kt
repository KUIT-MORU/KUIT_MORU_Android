package com.konkuk.moru.presentation.routinefeed.data

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.konkuk.moru.data.model.Routine

data class RoutineSectionModel(
    val title: String,
    val routines: List<Routine>
)

data class LiveUserInfo(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("nickname") // JSON의 "nickname" 필드를 "name" 변수에 매핑
    val name: String,

    @SerializedName("motivationTag") // JSON의 "motivationTag" 필드를 "tag" 변수에 매핑
    val tag: String,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)