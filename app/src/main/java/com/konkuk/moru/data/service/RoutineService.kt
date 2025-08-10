package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.response.RoutinePageResponse
import retrofit2.http.GET
import retrofit2.http.Query

// 내 루틴 목록 조회를 위한 service
interface RoutineService {
    @GET("/api/routines")
    suspend fun getMyRoutines(
        @Query("sortType") sortType: String = "TIME", // LATEST | POPULAR | TIME
        @Query("dayOfWeek") dayOfWeek: String,        // "MON" ~ "SUN"
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): RoutinePageResponse
}
