package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.response.RoutinePageResponse
import com.konkuk.moru.data.dto.response.RoutineDetailResponse
import com.konkuk.moru.data.dto.response.RoutineStepResponse
import com.konkuk.moru.data.mapper.toBackend
import com.konkuk.moru.data.service.RoutineService
import java.time.LocalDate
import javax.inject.Inject

class RoutineRepository @Inject constructor(
    private val service: RoutineService
) {
    suspend fun getMyRoutinesToday(page: Int = 0, size: Int = 20): RoutinePageResponse {
        val day = LocalDate.now().dayOfWeek.toBackend()
        return service.getMyRoutinesToday(sortType = "TIME", dayOfWeek = day, page = page, size = size)
    }

    suspend fun getRoutineDetail(routineId: String): RoutineDetailResponse {
        return service.getRoutineDetail(routineId)
    }

    suspend fun getRoutineSteps(routineId: String): List<RoutineStepResponse> {
        return service.getRoutineSteps(routineId)
    }
}
