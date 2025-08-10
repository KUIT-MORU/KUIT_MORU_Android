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
        val original = chain.request()

        if (original.header("Authorization") != null) {
            return chain.proceed(original)
        }

        val access = tokenManager.accessTokenBlocking()
        val req = if (!access.isNullOrEmpty()) {
            val short = if (access.length > 12) access.take(6) + "..." + access.takeLast(4) else access
            Log.d("AuthInterceptor", "Attach Authorization: Bearer $short")
            original.newBuilder()
                .header("Authorization", "Bearer $access")
                .build()
        } else {
            Log.w("AuthInterceptor", "No access token. Sending request without Authorization.")
            original
        }

        return chain.proceed(req)
    }
}
