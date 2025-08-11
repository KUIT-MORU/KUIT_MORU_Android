package com.konkuk.moru.data.repositoryimpl

import com.konkuk.moru.data.service.UserService
import com.konkuk.moru.data.service.UserMeResponse
import com.konkuk.moru.presentation.home.viewmodel.UserMe
import com.konkuk.moru.presentation.home.viewmodel.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {

    override suspend fun getMe(): UserMe {
        val res: UserMeResponse = userService.getMe()
        return UserMe(id = res.id, nickname = res.nickname)
    }
}
