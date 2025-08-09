package com.konkuk.moru.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess { (accessToken, refreshToken) ->
                LoginPreference.setLoggedIn(context, true)
                LoginPreference.saveAccessToken(context, accessToken)
                LoginPreference.saveRefreshToken(context, refreshToken)

                userSessionManager.setLoggedIn(true)

                _isLoggedIn.value = true

                onSuccess()
            }.onFailure { e ->
                onFailure(e.message ?: "로그인 실패")
            }
        }
    }
}