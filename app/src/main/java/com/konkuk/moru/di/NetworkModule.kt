package com.konkuk.moru.di

import com.konkuk.moru.BuildConfig
import com.konkuk.moru.data.interceptor.AuthInterceptor
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.data.service.InsightService
import com.konkuk.moru.data.service.NotificationService
import com.konkuk.moru.data.service.RoutineFeedService
import com.konkuk.moru.data.service.SearchService
import com.konkuk.moru.data.service.SocialService
import com.konkuk.moru.data.service.UserService
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
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authInterceptor: AuthInterceptor): Interceptor {
        return authInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // 바디까지 다 찍기 (개발용)
            level = HttpLoggingInterceptor.Level.BODY
            // 기본값은 Authorization 헤더가 마스킹됩니다
            // logging.redactHeader("Authorization") // 필요시 마스킹 유지
        }

        return UnsafeHttpClient.getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(authInterceptor) // 헤더 붙이기
            .addNetworkInterceptor { chain -> // ★ 응답 코드/URL 즉시 확인용
                val req = chain.request()
                android.util.Log.d("HTTP", "--> ${req.method} ${req.url}")
                val res = chain.proceed(req)
                android.util.Log.d("HTTP", "<-- ${res.code} ${res.message} ${req.url}")
                res
            }
            .addInterceptor(logging) // ★ BODY까지
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
    fun provideInsightService(retrofit: Retrofit): InsightService {
        return retrofit.create(InsightService::class.java)
    }

    @Provides
    @Singleton
    fun provideRoutineFeedService(retrofit: Retrofit): RoutineFeedService {
        return retrofit.create(RoutineFeedService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)


    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationService =
        retrofit.create(NotificationService::class.java)


    @Provides
    @Singleton
    fun provideSocialService(retrofit: Retrofit): SocialService =
        retrofit.create(SocialService::class.java)


    @Provides @Singleton
    fun provideSearchApi(retrofit: Retrofit): SearchService =
        retrofit.create(SearchService::class.java)



}
