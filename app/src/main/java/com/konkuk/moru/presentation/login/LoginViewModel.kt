package com.konkuk.moru.presentation.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.data.repositoryimpl.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _isOnboarding = MutableStateFlow<Boolean?>(null)
    val isOnboarding: StateFlow<Boolean?> = _isOnboarding

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                authRepo.loginAndSaveTokens(context, email, password)
                onResult(Result.success(Unit))
            } catch (e: Exception) {
                Log.e("LoginVM", "login failed", e)
                onResult(Result.failure(e))
            }
        }
    }
}
