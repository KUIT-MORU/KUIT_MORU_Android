package com.konkuk.moru.data.network

import android.content.Context
import com.konkuk.moru.data.network.interceptor.AuthInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.konkuk.moru.BuildConfig
import com.konkuk.moru.data.service.AuthService
import com.konkuk.moru.data.service.RoutineService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideAuthInterceptor(
        @ApplicationContext context: Context    // ← 여기에 @ApplicationContext 추가
    ): AuthInterceptor {
        return AuthInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        logging: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)

        if (BuildConfig.DEBUG) {
            // 🚨 개발용 임시 우회: 모든 인증서/호스트네임 신뢰 (절대 배포 금지)
            val trustAllCerts = arrayOf<javax.net.ssl.X509TrustManager>(
                object : javax.net.ssl.X509TrustManager {
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
                }
            )
            val sslContext = javax.net.ssl.SSLContext.getInstance("TLS").apply {
                init(null, trustAllCerts, java.security.SecureRandom())
            }
            builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
            builder.hostnameVerifier { _, _ -> true }
        }

        return builder.build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://15.164.150.204/")
            .client(client)
            .addConverterFactory(Json {
                ignoreUnknownKeys = true
            }.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideRoutineService(retrofit: Retrofit): RoutineService =
        retrofit.create(RoutineService::class.java)

}