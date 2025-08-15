package com.konkuk.moru.data.service

import com.konkuk.moru.data.dto.response.MyRoutine.MyPageResponse
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineDetailDto
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineScheduleDto
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineSummaryDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MyRoutineService {

    // [추가] 내 루틴 목록
    @GET("api/routines")
    suspend fun getMyRoutines(
        @Query("sortType") sortType: String = "TIME",  // TIME|LATEST|POPULAR
        @Query("dayOfWeek") dayOfWeek: String? = null, // TIME일 때 필요
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): MyPageResponse<MyRoutineSummaryDto>

    // [추가] 상세
    @GET("api/routines/{routineId}")
    suspend fun getRoutineDetail(@Path("routineId") routineId: String): MyRoutineDetailDto

    // [추가] 삭제
    @DELETE("api/routines/{routineId}")
    suspend fun deleteRoutine(@Path("routineId") routineId: String): Response<Unit>

    // [추가] 스케줄
    @GET("api/routines/{routineId}/schedules")
    suspend fun getSchedules(@Path("routineId") routineId: String): List<MyRoutineScheduleDto>

    @DELETE("api/routines/{routineId}/schedules")
    suspend fun deleteAllSchedules(@Path("routineId") routineId: String): Response<Unit>

    @DELETE("api/routines/{routineId}/schedules/{schId}")
    suspend fun deleteSchedule(
        @Path("routineId") routineId: String,
        @Path("schId") schId: String
    ): Response<Unit>
}