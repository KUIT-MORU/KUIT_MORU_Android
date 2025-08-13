package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.response.RoutinePageResponse
import com.konkuk.moru.data.dto.response.RoutineDetailResponse
import com.konkuk.moru.data.dto.response.RoutineStepResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// 내 루틴 목록 조회를 위한 service
interface RoutineService {
    @GET("/api/routines")
    suspend fun getMyRoutinesToday(
        @Query("sortType") sortType: String = "TIME", // LATEST | POPULAR | TIME
        @Query("dayOfWeek") dayOfWeek: String = "MON",        // "MON" ~ "SUN"
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): RoutinePageResponse

    @GET("/api/routines/{routineId}")
    suspend fun getRoutineDetail(@Path("routineId") routineId: String): RoutineDetailResponse

    @GET("/api/routines/{routineId}/steps")
    suspend fun getRoutineSteps(@Path("routineId") routineId: String): List<RoutineStepResponse>
}
