package com.konkuk.moru.domain.repository

import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedSectionModel

interface RoutineFeedRepository {
    suspend fun getLiveUsers(): List<LiveUserInfo>
    //suspend fun getRoutineSections(): List<RoutineFeedSectionModel>
}