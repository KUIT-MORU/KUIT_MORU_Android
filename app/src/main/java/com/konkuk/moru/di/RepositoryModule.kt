package com.konkuk.moru.di

import com.konkuk.moru.data.repositoryimpl.AuthRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.InsightRepositoryImpl
import com.konkuk.moru.domain.repository.AuthRepository
import com.konkuk.moru.domain.repository.InsightRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindInsightRepository(
        impl: InsightRepositoryImpl
    ): InsightRepository
}