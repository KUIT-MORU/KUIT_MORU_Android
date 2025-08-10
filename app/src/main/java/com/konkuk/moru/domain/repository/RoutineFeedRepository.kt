package com.konkuk.moru.domain.repository

import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineDetailResponse
import com.konkuk.moru.presentation.routinefeed.data.RoutineFeedResponse
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedSectionModel

interface RoutineFeedRepository {
    suspend fun getLiveUsers(): List<LiveUserInfo>
    suspend fun getRoutineFeed(): RoutineFeedResponse

    suspend fun getRoutineDetail(routineId: String): RoutineDetailResponse

    suspend fun addLike(routineId: String)
    suspend fun removeLike(routineId: String)
    suspend fun addScrap(routineId: String)
    suspend fun removeScrap(routineId: String)
}