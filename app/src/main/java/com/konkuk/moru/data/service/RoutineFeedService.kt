package com.konkuk.moru.data.service

import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import retrofit2.http.GET

interface RoutineFeedService {

    @GET("api/routines/live-users")
    suspend fun getLiveUsers(): List<LiveUserInfo>

    // 🚨 실제 서버 API 명세에 맞게 엔드포인트를 수정해야 합니다.
    /* @GET("insight/routine-sections")
     suspend fun getRoutineSections(): List<RoutineFeedSectionModel>*/
}