package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.dto.response.UserProfileResponse
import com.konkuk.moru.data.service.UserService
import com.konkuk.moru.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val service: UserService
) : UserRepository {

    override suspend fun getUserProfile(): UserProfileResponse {
        return service.getMe()
    }
}
