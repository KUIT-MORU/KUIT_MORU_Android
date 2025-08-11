package com.konkuk.moru.di

import com.konkuk.moru.BuildConfig
import com.konkuk.moru.data.interceptor.AuthInterceptor
import com.konkuk.moru.data.interceptor.TokenAuthenticator
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.data.service.InsightService
import com.konkuk.moru.data.service.RoutineService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides @Singleton @Named("authlessOkHttp")
    fun provideAuthlessOkHttp(): OkHttpClient =
        UnsafeHttpClient.getUnsafeOkHttpClient()

    @Provides @Singleton @Named("authlessRetrofit")
    fun provideAuthlessRetrofit(
        @Named("authlessOkHttp") ok: OkHttpClient,
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(ok)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides @Singleton
    fun provideAuthInterceptor(authInterceptor: AuthInterceptor): Interceptor = authInterceptor

    @Provides @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        UnsafeHttpClient.getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()

    @Provides @Singleton
    fun provideRetrofit(baseUrl: String, okHttp: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttp)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides @Singleton
    fun provideInsightService(retrofit: Retrofit): InsightService =
        retrofit.create(InsightService::class.java)

    @Provides
    @Singleton
    fun provideRoutineService(retrofit: Retrofit): RoutineService =
        retrofit.create(RoutineService::class.java)

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): com.konkuk.moru.data.service.UserService =
        retrofit.create(com.konkuk.moru.data.service.UserService::class.java)

}
