package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.request.CreateRoutineRequest
import com.konkuk.moru.data.dto.response.CreateRoutineResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CreateRoutineService {
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("/api/routines")
    suspend fun createRoutine(
        @Body body: CreateRoutineRequest
    ): Response<CreateRoutineResponse>
}