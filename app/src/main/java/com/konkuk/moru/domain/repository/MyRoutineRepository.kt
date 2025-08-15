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
    suspend fun deleteRoutine(routineId: String)

    suspend fun getSchedules(routineId: String): List<MyRoutineSchedule>
    suspend fun deleteAllSchedules(routineId: String)
    suspend fun deleteSchedule(routineId: String, scheduleId: String)
}

// [추가] 스케줄 도메인도 접두어
data class MyRoutineSchedule(
    val id: String,
    val dayOfWeek: String, // "MON"...
    val time: String,      // "HH:mm:ss"
    val alarmEnabled: Boolean
)