package com.konkuk.moru.di

import com.konkuk.moru.BuildConfig
import com.konkuk.moru.data.interceptor.AuthInterceptor
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.data.service.InsightService
import com.konkuk.moru.data.service.RoutineFeedService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authInterceptor: AuthInterceptor): Interceptor {
        return authInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        return UnsafeHttpClient.getUnsafeOkHttpClient().newBuilder() // 테스트용 클라이언트 기반
            .addInterceptor(authInterceptor) // ✅ Authorization 헤더 자동 삽입
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideInsightService(retrofit: Retrofit): InsightService {
        return retrofit.create(InsightService::class.java)
    }

    @Provides
    @Singleton
    fun provideRoutineFeedService(retrofit: Retrofit): RoutineFeedService {
        return retrofit.create(RoutineFeedService::class.java)
    }


}
