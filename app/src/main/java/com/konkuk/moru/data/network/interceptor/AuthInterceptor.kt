// AuthInterceptor.kt
package com.konkuk.moru.data.network.interceptor

import android.content.Context
import com.konkuk.moru.BuildConfig
import com.konkuk.moru.core.datastore.TokenPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log

class AuthInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            TokenPreference.getAccessToken(context).first()
        }

        val reqBuilder = chain.request().newBuilder()

        if (!accessToken.isNullOrBlank()) {
            reqBuilder.header("Authorization", "Bearer $accessToken")
            Log.d("AuthInterceptor", "Authorization attached. len=${accessToken.length}")
        } else if (BuildConfig.DEBUG) {
            // ⚠️ 디버그 전용 임시 토큰 (서버에서 받은 유효한 JWT로 교체)
            val devToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwNWM0NDg2Ni1kYTYwLTQ2YTgtYWVkNC1lZGZjMDE0MjIzNWIiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTc1NDY1ODU5OSwiZXhwIjoxNzU0NjYyMTk5fQ.6Y5UtEL8sIVIUi1E_lwHSWrbIrEp7iBtHiLdCQTHwO8"
            reqBuilder.header("Authorization", "Bearer $devToken")
            Log.w("AuthInterceptor", "No accessToken → DEV token attached.")
        } else {
            Log.w("AuthInterceptor", "No accessToken. Authorization header skipped.")
        }

        return chain.proceed(reqBuilder.build())
    }
}
