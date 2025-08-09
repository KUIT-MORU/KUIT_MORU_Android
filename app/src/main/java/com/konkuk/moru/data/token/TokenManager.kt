package com.konkuk.moru.data.token

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        Log.d("TokenManager", "saveToken() called with: $token")
        prefs.edit().putString("access_token", token).apply()
    }

    fun getToken(): String? {
        val token = prefs.getString("access_token", null)
        Log.d("TokenManager", "getToken() returned: $token")
        return token
    }

    fun clearToken() {
        prefs.edit().remove("access_token").apply()
    }
}