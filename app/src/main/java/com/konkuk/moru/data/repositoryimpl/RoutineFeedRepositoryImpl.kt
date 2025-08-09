package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.service.RoutineFeedService
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedSectionModel
import javax.inject.Inject

class RoutineFeedRepositoryImpl @Inject constructor(
    private val routineFeedService: RoutineFeedService
) : RoutineFeedRepository {

    override suspend fun getLiveUsers(): List<LiveUserInfo> {
        return routineFeedService.getLiveUsers()
    }

   /* override suspend fun getRoutineSections(): List<RoutineFeedSectionModel> {
        return routineFeedService.getRoutineSections()
    }*/
}