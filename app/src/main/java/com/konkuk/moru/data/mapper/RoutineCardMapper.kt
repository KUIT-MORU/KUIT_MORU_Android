package com.konkuk.moru.data.mapper

import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineCardDomain

/**
 * Domain → UI 모델 매핑
 * UI에서 사용하는 Routine(피드 카드용)으로 가볍게 채워 넣습니다.
 * authorId는 프로필 주인 id로 세팅(상세로 넘어갈 때 작성자 프로필 이동 등에 사용 가능)
 */
fun RoutineCardDomain.toUiRoutine(
    profileOwnerId: String,
    authorName: String,
    authorProfileUrl: String?
): Routine =
    Routine(
        routineId = id,
        title = title,
        imageUrl = imageUrl,
        tags = tags,
        likes = likeCount,
        isRunning = isRunning,

        // UI에서 필요하지만 서버 카드 응답에 없는 값들은 기본값으로
        description = "",
        category = "일상",
        authorId = profileOwnerId ?: "", // 프로필 화면 주인의 id
        authorName = authorName,
        authorProfileUrl = authorProfileUrl,
        isLiked = false,
        isBookmarked = false,

        isChecked = false,
        scheduledTime = null,
        scheduledDays = emptySet(),
        isAlarmEnabled = false,
        steps = emptyList(),
        similarRoutines = emptyList(),
        usedApps = emptyList()
    )