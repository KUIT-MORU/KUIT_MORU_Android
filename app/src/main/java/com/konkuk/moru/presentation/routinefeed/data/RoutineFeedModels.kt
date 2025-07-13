package com.konkuk.moru.presentation.routinefeed.data

import androidx.annotation.DrawableRes
import com.konkuk.moru.data.model.Routine

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
    val routines: List<Routine>
)
// 전체 Routine으로 수정해놈

data class LiveUserInfo(
    val id: Int,
    val name: String,
    val tag: String,
    @DrawableRes val profileImageRes: Int
)