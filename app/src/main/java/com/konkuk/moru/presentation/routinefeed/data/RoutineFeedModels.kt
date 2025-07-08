package com.konkuk.moru.presentation.routinefeed.data

import androidx.annotation.DrawableRes

data class LiveUserInfo(
    val id: Int,
    val name: String,
    val tag: String,
    @DrawableRes val profileImageRes: Int
)

data class RoutineInfo(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val likes: Int,
    val isRunning: Boolean,
    var isLiked: Boolean
)

data class RoutineSectionModel(
    val title: String,
    val routines: List<RoutineInfo>
)