package com.konkuk.moru.data.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
    }

    /** -------- 읽기 (suspend) -------- */
    suspend fun accessToken(): String? =
        context.dataStore.data.map { it[Keys.ACCESS] }.first()

    suspend fun refreshToken(): String? =
        context.dataStore.data.map { it[Keys.REFRESH] }.first()

    /** -------- 읽기 (blocking) : Interceptor/Authenticator 용 -------- */
    fun accessTokenBlocking(): String? = runBlocking { accessToken() }
    fun refreshTokenBlocking(): String? = runBlocking { refreshToken() }

    /** -------- 저장 -------- */
    // 로그인 직후나 갱신 성공 시 원자적으로 저장
    suspend fun saveTokens(access: String, refresh: String?) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS] = access
            if (refresh != null) prefs[Keys.REFRESH] = refresh
        }
    }

    // access 만 갱신(서버가 refresh 를 안 주는 타입의 갱신 응답일 때)
    suspend fun updateAccess(access: String) {
        context.dataStore.edit { prefs -> prefs[Keys.ACCESS] = access }
    }

    /** -------- 삭제(로그아웃) -------- */
    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS)
            prefs.remove(Keys.REFRESH)
        }
    }
}
