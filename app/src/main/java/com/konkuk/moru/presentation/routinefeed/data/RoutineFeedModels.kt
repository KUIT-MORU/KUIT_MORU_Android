package com.konkuk.moru.presentation.routinefeed.data

import androidx.annotation.DrawableRes

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

/* ---------- 모델 ---------- */
data class HotRoutine(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val likes: Int,
    val isLiked: Boolean,
    val isRunning: Boolean
)

data class LiveUserInfo(
    val id: Int,
    val name: String,
    val tag: String,
    @DrawableRes val profileImageRes: Int
)