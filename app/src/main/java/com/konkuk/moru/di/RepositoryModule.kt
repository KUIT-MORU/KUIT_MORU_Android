package com.konkuk.moru.di

import com.konkuk.moru.data.repositoryimpl.AuthRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.InsightRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.RoutineFeedRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.UserRepositoryImpl
import com.konkuk.moru.domain.repository.AuthRepository
import com.konkuk.moru.domain.repository.InsightRepository
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import com.konkuk.moru.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindInsightRepository(
        impl: InsightRepositoryImpl
    ): InsightRepository

    @Binds
    @Singleton
    abstract fun bindRoutineFeedRepository(
        impl: RoutineFeedRepositoryImpl
    ): RoutineFeedRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

}