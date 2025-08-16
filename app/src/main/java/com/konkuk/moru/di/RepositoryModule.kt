package com.konkuk.moru.di

import com.konkuk.moru.data.repositoryimpl.AuthRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.InsightRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.MyActTagRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.NotificationRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.OBUserRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.RoutineFeedRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.RoutineUserRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.SearchRepositoryImpl
import com.konkuk.moru.data.repositoryimpl.SocialRepositoryImpl
import com.konkuk.moru.domain.repository.AuthRepository
import com.konkuk.moru.domain.repository.InsightRepository
import com.konkuk.moru.domain.repository.MyActTagRepository
import com.konkuk.moru.domain.repository.NotificationRepository
import com.konkuk.moru.domain.repository.OBUserRepository
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import com.konkuk.moru.domain.repository.RoutineUserRepository
import com.konkuk.moru.domain.repository.SearchRepository
import com.konkuk.moru.domain.repository.SocialRepository
import com.konkuk.moru.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindOBUserRepository(impl: OBUserRepositoryImpl): OBUserRepository

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
    abstract fun bindRoutineUserRepository(
        impl: RoutineUserRepositoryImpl
    ): RoutineUserRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(impl: SocialRepositoryImpl): SocialRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository


    @Binds
    @Singleton
    abstract fun bindMyActTagRepository(impl: MyActTagRepositoryImpl): MyActTagRepository

}