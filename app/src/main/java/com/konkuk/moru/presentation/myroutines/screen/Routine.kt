package com.konkuk.moru.presentation.myroutines.screen

import java.time.DayOfWeek
import java.time.LocalTime


data class MyRoutine(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val likes: Int,
    var isLiked: Boolean,
    val isRunning: Boolean,
    val isChecked: Boolean = false,
    val scheduledTime: LocalTime? = null,
    val scheduledDays: Set<DayOfWeek> = emptySet()
)


// ❗️ 추가: 인메모리 상태 관리를 위한 데이터 클래스
data class Routine(
    val id: Int,
    val name: String,
    val tags: List<String>,
    var likes: Int,
    var isLiked: Boolean,
    val isRunning: Boolean,
    // --- 상태 관리를 위해 추가된 속성 ---
    var isChecked: Boolean = false,
    var scheduledTime: LocalTime? = null,
    var scheduledDays: Set<DayOfWeek> = emptySet(),
    // 최신순 정렬을 위해 생성 시간을 나타내는 필드가 있으면 좋습니다. (없으면 id로 대체)
    val createdAt: Long = System.currentTimeMillis()
)

