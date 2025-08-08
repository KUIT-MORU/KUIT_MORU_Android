package com.konkuk.moru.data.interceptor

import android.util.Log
import com.konkuk.moru.data.token.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()

        val newRequest = chain.request().newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                Log.d("AuthInterceptor", "Token attached: Bearer $token")
                addHeader("Authorization", "Bearer $token")
            } else {
                Log.w("AuthInterceptor", "No token found. Skipping Authorization header.")
            }
        }.build()

        return chain.proceed(newRequest)
    }

}