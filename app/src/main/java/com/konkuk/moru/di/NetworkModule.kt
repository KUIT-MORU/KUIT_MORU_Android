package com.konkuk.moru.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides @Singleton @Named("authlessOkHttp")
    fun provideAuthlessOkHttp(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        UnsafeHttpClient.getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideJson(): Json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    @Provides @Singleton @Named("authlessRetrofit")
    fun provideAuthlessRetrofit(
        @Named("authlessOkHttp") ok: OkHttpClient,
        baseUrl: String,
        json: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(ok)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides @Singleton
    fun provideAuthInterceptor(authInterceptor: AuthInterceptor): Interceptor = authInterceptor

    @Provides @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        UnsafeHttpClient.getUnsafeOkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    @Provides @Singleton
    fun provideRetrofit(baseUrl: String, okHttp: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
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
    fun provideUserService(retrofit: Retrofit): com.konkuk.moru.data.service.HomeUserService =
        retrofit.create(com.konkuk.moru.data.service.HomeUserService::class.java)

}
