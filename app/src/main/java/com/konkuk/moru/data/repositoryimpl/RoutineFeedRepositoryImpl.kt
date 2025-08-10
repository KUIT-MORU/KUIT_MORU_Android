package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.service.RoutineFeedService
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.data.RoutineDetailResponse
import com.konkuk.moru.presentation.routinefeed.data.RoutineFeedResponse

import javax.inject.Inject

class RoutineFeedRepositoryImpl @Inject constructor(
    private val routineFeedService: RoutineFeedService
) : RoutineFeedRepository {

    override suspend fun getLiveUsers(): List<LiveUserInfo> {
        return routineFeedService.getLiveUsers()
    }

    override suspend fun getRoutineFeed(): RoutineFeedResponse {
        return routineFeedService.getRoutineFeed()
    }

    override suspend fun getRoutineDetail(routineId: String): RoutineDetailResponse =
        routineFeedService.getRoutineDetail(routineId)

    override suspend fun addLike(routineId: String) = routineFeedService.addLike(routineId)
    override suspend fun removeLike(routineId: String) = routineFeedService.removeLike(routineId)
    override suspend fun addScrap(routineId: String) = routineFeedService.addScrap(routineId)
    override suspend fun removeScrap(routineId: String) = routineFeedService.removeScrap(routineId)

}