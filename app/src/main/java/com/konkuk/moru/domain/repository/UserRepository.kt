package com.konkuk.moru.domain.repository

import com.konkuk.moru.data.model.UserProfileDomain


interface UserRepository {
    suspend fun getMe(): UserProfileDomain
    suspend fun getUserProfile(userId: String): UserProfileDomain
}

