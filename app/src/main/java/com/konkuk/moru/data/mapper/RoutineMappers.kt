package com.konkuk.moru.data.mapper

import com.konkuk.moru.data.dto.response.RoutineResponse
import com.konkuk.moru.data.dto.response.RoutineStepResponse
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import java.time.DayOfWeek

// 서버가 dayOfWeek=MON~SUN으로 필터링해서 내려주므로,
// 클라이언트는 정렬/요일 계산을 하지 않고 "응답 순서" 그대로 사용한다.
fun RoutineResponse.toDomain(): Routine =
    Routine(
        routineId = routineId,             // 서버 String ID를 그대로 사용
        title = title,
        description = "",                  // 목록 응답에 없으므로 기본값
        imageUrl = null,                   // 필요 시 서버 필드 추가되면 매핑
        category = category,
        tags = tags,
        authorId = "me",                   // DummyData 제거: 임시 고정값(프로필 연동 시 교체)
        authorName = "",                   // 필요 시 유저 프로필에서 채움
        authorProfileUrl = null,
        likes = 0,
        isLiked = false,
        isBookmarked = false,
        isRunning = false,
        scheduledDays = emptySet<DayOfWeek>(),
        scheduledTime = null,
        steps = steps.map { it.toDomain() }
    )

private fun RoutineStepResponse.toDomain(): RoutineStep =
    RoutineStep(name = name, duration = duration) // "MM:SS"
