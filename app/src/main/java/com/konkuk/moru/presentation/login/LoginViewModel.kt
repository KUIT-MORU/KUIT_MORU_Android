package com.konkuk.moru.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LoginPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 실제 로그인 API 호출 후 성공했다고 가정
            val isLoginSuccessful = true

            if (isLoginSuccessful) {
                LoginPreference.setLoggedIn(context, true)
                onSuccess()
            }
        }
    }
}