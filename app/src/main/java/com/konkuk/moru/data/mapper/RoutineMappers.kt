package com.konkuk.moru.data.mapper

import com.konkuk.moru.data.dto.response.RoutineResponse
import com.konkuk.moru.data.model.Routine
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 서버가 dayOfWeek=MON~SUN으로 필터링해서 내려주므로,
// 클라이언트는 정렬/요일 계산을 하지 않고 "응답 순서" 그대로 사용한다.
fun RoutineResponse.toDomain(): Routine {
    // 서버에서 받은 이미지 URL 로깅
    android.util.Log.d("RoutineMapper", "🖼️ 서버에서 받은 루틴 이미지 URL: ${imageUrl ?: "null"} (제목: $title)")

    return Routine(
        routineId = routineId,             // 서버 String ID를 그대로 사용
        title = title,
        description = "",                  // 목록 응답에 없으므로 기본값
        imageUrl = imageUrl,               // 서버에서 받은 imageUrl 사용
        category = category ?: "",         // 서버에서 받은 category 사용, 없으면 빈 문자열
        tags = tags,
        authorId = "me",                   // DummyData 제거: 임시 고정값(프로필 연동 시 교체)
        authorName = "",                   // 필요 시 유저 프로필에서 채움
        authorProfileUrl = null,
        likes = likeCount,                 // 서버에서 받은 likeCount 사용
        isLiked = false,
        isBookmarked = false,
        isRunning = isRunning,             // 서버에서 받은 isRunning 사용
        isSimple = isSimple,               // 서버에서 받은 isSimple 사용
        requiredTime = requiredTime ?: "", // 서버에서 받은 requiredTime 사용
        scheduledDays = scheduledDays.mapNotNull { dayString ->
            when (dayString.uppercase()) {
                "MON" -> DayOfWeek.MONDAY
                "TUE" -> DayOfWeek.TUESDAY
                "WED" -> DayOfWeek.WEDNESDAY
                "THU" -> DayOfWeek.THURSDAY
                "FRI" -> DayOfWeek.FRIDAY
                "SAT" -> DayOfWeek.SATURDAY
                "SUN" -> DayOfWeek.SUNDAY
                else -> null
            }
        }.toSet(),
        scheduledTime = scheduledTime?.let { timeString ->
            try {
                LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) {
                null
            }
        },
        steps = emptyList()                // 서버 응답에 없으므로 빈 리스트
    )
}