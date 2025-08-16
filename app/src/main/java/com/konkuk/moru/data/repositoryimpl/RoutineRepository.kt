package com.konkuk.moru.data.repositoryimpl

import android.util.Log
import com.konkuk.moru.data.dto.response.RoutinePageResponse
import com.konkuk.moru.data.dto.response.Routine.RoutineDetailResponseV1
import com.konkuk.moru.data.dto.response.RoutineStepResponse
import com.konkuk.moru.data.dto.response.HomeScheduleResponse
import com.konkuk.moru.data.mapper.toBackend
import com.konkuk.moru.data.service.RoutineService
import java.time.LocalDate
import javax.inject.Inject

class RoutineRepository @Inject constructor(
    private val service: RoutineService
) {

    private companion object {
        private const val TAG = "RoutineRepository"
    }
    suspend fun getMyRoutinesToday(page: Int = 0, size: Int = 20): RoutinePageResponse {
        val day = LocalDate.now().dayOfWeek.toBackend()
        return service.getMyRoutinesToday(sortType = "TIME", dayOfWeek = day, page = page, size = size)
    }

    suspend fun getRoutineDetail(routineId: String): RoutineDetailResponseV1 {
        return service.getRoutineDetail(routineId)
    }

    suspend fun getRoutineSteps(routineId: String): List<RoutineStepResponse> {
        return service.getRoutineSteps(routineId)
    }

    suspend fun getRoutineSchedules(routineId: String): List<HomeScheduleResponse> {
        Log.d(TAG, "🔄 Repository getRoutineSchedules 호출: routineId=$routineId")
        return try {
            val schedules = service.getRoutineSchedules(routineId)
            Log.d(TAG, "✅ Repository 스케줄 정보 가져오기 성공: ${schedules.size}개")
            schedules
        } catch (e: Exception) {
            Log.e(TAG, "❌ Repository 스케줄 정보 가져오기 실패: routineId=$routineId", e)
            throw e
        }
    }
}
