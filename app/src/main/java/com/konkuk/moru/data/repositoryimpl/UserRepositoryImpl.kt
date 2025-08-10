package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.response.UserProfileResponse
import com.konkuk.moru.data.mapper.toDomain
import com.konkuk.moru.data.model.RoutineCardDomain
import com.konkuk.moru.data.model.UserProfile
import com.konkuk.moru.data.model.UserProfileDomain
import com.konkuk.moru.data.service.UserService
import com.konkuk.moru.domain.repository.UserRepository
import com.konkuk.moru.presentation.routinefeed.data.UserMeResponse
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {
    override suspend fun getMe(): UserProfileDomain = userService.getMe().toDomain()

    override suspend fun getUserProfile(userId: String): UserProfileDomain =
        userService.getUserProfile(userId).toDomain()
}


