package com.konkuk.moru.data.mapper

import com.konkuk.moru.core.datastore.LikeMemory
import com.konkuk.moru.core.datastore.SocialMemory
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.routinefeed.data.RoutineInfo


fun RoutineInfo.toRoutineModel(): Routine {
    val mem = SocialMemory.getRoutine(this.id) // [추가]

    return Routine(
        routineId = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        tags = this.tags,

        // [변경] likeCount도 메모리 우선
        likes = mem?.likeCount ?: likeCount,
        isRunning = this.isRunning,

        // [변경] 전역 메모리 우선, 없으면 false
        isLiked = mem?.isLiked ?: false,

        // 이하 동일
        description = "Description not available",
        category = "일상",
        authorId = "",
        authorName = "Unknown",
        authorProfileUrl = null,
        isBookmarked = mem?.isScrapped ?: false, // [추가]
        isChecked = false,
        scheduledTime = null,
        scheduledDays = emptySet(),
        isAlarmEnabled = false,
        steps = emptyList(),
        similarRoutines = emptyList(),
        usedApps = emptyList()
    )
}