package com.konkuk.moru.data.service

import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineFeedResponse
import retrofit2.http.GET

interface RoutineFeedService {

    @GET("api/routines/live-users")
    suspend fun getLiveUsers(): List<LiveUserInfo>

    // 🚨 실제 서버 API 명세에 맞게 엔드포인트를 수정해야 합니다.
    @GET("/api/routines/recommend/feed") // 예시 경로입니다. 실제 경로로 변경해주세요.
    suspend fun getRoutineFeed(): RoutineFeedResponse
}