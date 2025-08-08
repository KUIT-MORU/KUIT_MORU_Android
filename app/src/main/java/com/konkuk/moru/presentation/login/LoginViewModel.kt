package com.konkuk.moru.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess { (accessToken, refreshToken) ->
                LoginPreference.setLoggedIn(context, true)
                LoginPreference.saveAccessToken(context, accessToken)
                LoginPreference.saveRefreshToken(context, refreshToken)

                onSuccess()
            }.onFailure { e ->
                onFailure(e.message ?: "로그인 실패")
            }
        }
    }
}
