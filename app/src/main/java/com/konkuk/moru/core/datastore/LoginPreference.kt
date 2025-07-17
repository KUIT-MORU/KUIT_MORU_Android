package com.konkuk.moru.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LOGIN_PREFS = "login_prefs"
private val Context.loginDataStore by preferencesDataStore(name = LOGIN_PREFS)

object LoginPreference {
    private val LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

    fun isLoggedIn(context: Context): Flow<Boolean> {
        return context.loginDataStore.data.map { prefs ->
            prefs[LOGGED_IN_KEY] ?: false
        }
    }

    suspend fun setLoggedIn(context: Context, value: Boolean) {
        context.loginDataStore.edit { prefs ->
            prefs[LOGGED_IN_KEY] = value
        }
    }
}