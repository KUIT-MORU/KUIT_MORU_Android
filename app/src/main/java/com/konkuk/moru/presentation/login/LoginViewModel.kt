package com.konkuk.moru.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.domain.repository.AuthRepository
import com.konkuk.moru.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
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

                // 로그인 성공 후 사용자 정보 로드
                try {
                    val userProfile = userRepository.getUserProfile()
                    // 사용자 정보를 세션에 저장
                    userSessionManager.setUserProfile(userProfile)
                } catch (e: Exception) {
                    // 사용자 정보 로드 실패는 로그인 실패로 처리하지 않음
                    // 로그인은 성공했으므로 계속 진행
                }

                onSuccess()
            }.onFailure { e ->
                onFailure(e.message ?: "로그인 실패")
            }
        }
    }
}