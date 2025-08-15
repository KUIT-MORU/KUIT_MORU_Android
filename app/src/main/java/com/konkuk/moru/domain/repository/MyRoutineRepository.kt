package com.konkuk.moru.domain.repository

import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineUi
import java.time.DayOfWeek

interface MyRoutineRepository {
    suspend fun getMyRoutines(
        sortType: String,        // "TIME" | "LATEST" | "POPULAR"
        dayOfWeek: DayOfWeek?,   // TIME일 때만 사용
        page: Int,
        size: Int
    ): List<MyRoutineUi>

    suspend fun getRoutineDetail(routineId: String): MyRoutineUi

    suspend fun getSchedules(routineId: String): List<MyRoutineSchedule>

    // ✅ 안전 삭제 (스케줄 먼저 지우고 재시도)
    suspend fun deleteRoutineSafe(routineId: String): Boolean
    suspend fun deleteRoutine(routineId: String)

    suspend fun deleteAllSchedules(routineId: String)
    suspend fun deleteSchedule(routineId: String, scheduleId: String)

    // ✅ PATCH: 스케줄 수정
    suspend fun updateSchedule(
        routineId: String,
        schId: String,
        time: String,                 // "HH:mm:ss"
        days: Set<DayOfWeek>,
        alarm: Boolean
    ): List<MyRoutineSchedule>
}

// [추가] 스케줄 도메인도 접두어
data class MyRoutineSchedule(
    val id: String,
    val dayOfWeek: String, // "MON"...
    val time: String,      // "HH:mm:ss"
    val alarmEnabled: Boolean
)