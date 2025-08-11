package com.konkuk.moru.data.mapper


import com.konkuk.moru.data.dto.response.RoutineDetailResponse
import com.konkuk.moru.data.model.AppInfo
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import com.konkuk.moru.data.model.SimilarRoutine
import com.konkuk.moru.presentation.routinefeed.data.AppDto

import com.konkuk.moru.presentation.routinefeed.data.RoutineStepDto
import com.konkuk.moru.presentation.routinefeed.data.SimilarRoutineItemDto
import java.time.Duration

// RoutineDetailResponse -> Routine (앱 공용 모델)
private fun firstNonBlank(vararg s: String?): String? =
    s.firstOrNull { !it.isNullOrBlank() }

// RoutineDetailResponse -> Routine
fun RoutineDetailResponse.toRoutineModel(prev: Routine? = null): Routine {
    val base = prev ?: emptyRoutine(id)

    val resolvedAuthorId = firstNonBlank(author?.id, base.authorId) ?: ""
    val resolvedAuthorName = firstNonBlank(author?.nickname, base.authorName, "알 수 없음") ?: "알 수 없음"
    val resolvedAuthorProfile = firstNonBlank(author?.profileImageUrl, base.authorProfileUrl)

    return base.copy(
        routineId = id,
        title = firstNonBlank(title, base.title) ?: "",
        description = firstNonBlank(description, base.description) ?: "",
        imageUrl = firstNonBlank(imageUrl, base.imageUrl),
        category = if (isSimple) "간편" else "집중",
        tags = tags.orEmpty().ifEmpty { base.tags },
        likes = likeCount,

        isLiked = this.isLiked ?: base.isLiked,
        isBookmarked = this.isScrapped ?: base.isBookmarked,
        scrapCount = scrapCount,

        steps = steps.orEmpty()                                              // [변경]
            .sortedBy { it.stepOrder }
            .map { it.toStepModel() },
        usedApps = apps.orEmpty().map { it.toAppModel() },

        authorId = resolvedAuthorId,
        authorName = resolvedAuthorName,
        authorProfileUrl = resolvedAuthorProfile
    )
}

private fun emptyRoutine(routineId: String) = Routine(
    routineId = routineId,
    title = "",
    imageUrl = null,
    tags = emptyList(),
    likes = 0,


    description = "",
    category = "일상",
    authorId = "",
    authorName = "",
    authorProfileUrl = null,

    isLiked = false,
    isBookmarked = false,
    isRunning = false,
    isChecked = false,
    scheduledTime = null,
    scheduledDays = emptySet(),
    isAlarmEnabled = false,

    steps = emptyList(),
    similarRoutines = emptyList(),
    usedApps = emptyList()
)


// SimilarRoutineItemDto -> UI용 SimilarRoutine
fun SimilarRoutineItemDto.toUiModel(): SimilarRoutine {
    val picked = when {
        !tag.isNullOrBlank() -> tag
        !tags.isNullOrEmpty() -> tags.first()
        else -> null
    }
    return SimilarRoutine(
        id = id,
        imageUrl = imageUrl,
        name = title,
        tag = picked?.let { "#$it" } ?: "#루틴"
    )
}

private fun RoutineStepDto.toStepModel(): RoutineStep {
    return RoutineStep(
        name = name,
        duration = parseIsoDurationToClock(estimatedTime) // "PT5M" -> "05:00"
    )
}

private fun AppDto.toAppModel(): AppInfo {
    return AppInfo(
        name = name,
        iconUrl = null, // [TODO] 서버가 앱 아이콘 URL 제공 시 매핑
        packageName = packageName
    )
}

// "PT50M" / "PT5M30S" -> "MM:SS" (예: 50:00 / 05:30)
private fun parseIsoDurationToClock(iso: String?): String {
    if (iso.isNullOrBlank()) return "00:00"
    return runCatching {
        val d = Duration.parse(iso)
        val totalSec = d.seconds
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        String.format("%02d:%02d", minutes, seconds)
    }.getOrElse { "00:00" }
}