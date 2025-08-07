package com.konkuk.moru.data.network.interceptor

import android.content.Context
import com.konkuk.moru.core.datastore.TokenPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            // Flow<String?> 에서 첫 번째(최신) 값을 가져옴
            TokenPreference.getAccessToken(context).first()
        }

        val newRequest = chain.request().newBuilder()
            .apply {
                // 이제 String? 타입이므로 isNullOrBlank() 사용 가능
                if (!accessToken.isNullOrBlank()) {
                    header("Authorization", "Bearer $accessToken")
                }
            }
            .build()

        return chain.proceed(newRequest)
    }
}
