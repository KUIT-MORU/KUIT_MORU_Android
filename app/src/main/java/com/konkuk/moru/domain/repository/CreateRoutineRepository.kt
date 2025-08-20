package com.konkuk.moru.domain.repository

import com.konkuk.moru.data.dto.request.CreateRoutineRequest
import com.konkuk.moru.data.dto.response.CreateRoutineResponse

interface CreateRoutineRepository {
    suspend fun createRoutine(body: CreateRoutineRequest): Result<CreateRoutineResponse>
}