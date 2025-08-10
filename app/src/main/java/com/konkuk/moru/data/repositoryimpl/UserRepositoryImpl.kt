package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.mapper.toDomain
import com.konkuk.moru.data.model.UserProfileDomain
import com.konkuk.moru.data.service.UserService
import com.konkuk.moru.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {
    override suspend fun getMe(): UserProfileDomain = userService.getMe().toDomain()

    override suspend fun getUserProfile(userId: String): UserProfileDomain =
        userService.getUserProfile(userId).toDomain()
}


