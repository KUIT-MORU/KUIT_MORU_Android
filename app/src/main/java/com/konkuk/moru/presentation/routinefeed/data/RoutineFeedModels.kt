package com.konkuk.moru.presentation.routinefeed.data

import androidx.annotation.DrawableRes
import com.konkuk.moru.data.model.Routine

data class RoutineSectionModel(
    val title: String,
    val routines: List<Routine>
)

data class LiveUserInfo(
    val userId: String,
    val name: String,
    val tag: String,
    val profileImageUrl: String?
)