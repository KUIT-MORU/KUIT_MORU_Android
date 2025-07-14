package com.konkuk.moru.presentation.routinefeed.data

import androidx.annotation.DrawableRes
import com.konkuk.moru.data.model.Routine

data class RoutineSectionModel(
    val title: String,
    val routines: List<Routine>
)

data class LiveUserInfo(
    val id: Int,
    val name: String,
    val tag: String,
    @DrawableRes val profileImageRes: Int
)