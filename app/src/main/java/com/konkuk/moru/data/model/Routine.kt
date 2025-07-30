package com.konkuk.moru.data.model

import java.time.DayOfWeek
import java.time.LocalTime

/**
 * 앱 전체에서 사용될 통합 루틴 데이터 클래스
 */
data class Routine(
    // 기본 정보
    val routineId: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val category: String,
    val tags: List<String>,

    // 사용자 관련 정보
    val authorId: Int,
    val authorName: String,
    val authorProfileUrl: String?,

    // 상태 정보
    var likes: Int,
    var isLiked: Boolean,
    var isBookmarked: Boolean,
    val isRunning: Boolean, // 현재 사용자가 이 루틴을 실행 중인지
    var isChecked: Boolean = false, // '내 루틴' 삭제 모드에서 사용

    // 시간 정보 (내 루틴)
    val scheduledTime: LocalTime? = null,
    val scheduledDays: Set<DayOfWeek> = emptySet(),
    var isAlarmEnabled: Boolean = false,

    // 상세 화면용 정보
    val steps: List<RoutineStep> = emptyList(),
    val similarRoutines: List<SimilarRoutine> = emptyList(),

    // 사용앱 관련 정보
    val usedApps: List<AppInfo> = emptyList()
)

data class AppInfo(
    val name: String,
    val iconUrl: String?
)

/**
 * 루틴의 각 단계를 나타내는 클래스
 */
data class RoutineStep(
    val name: String,
    val duration: String // "00:00" 형식
)

/**
 * 비슷한 루틴 정보를 나타내는 클래스
 */
data class SimilarRoutine(
    val id: Int,
    val imageUrl: String?,
    val name: String,
    val tag: String
)