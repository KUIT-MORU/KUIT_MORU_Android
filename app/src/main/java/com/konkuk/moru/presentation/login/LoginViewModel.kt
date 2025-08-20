// file: presentation/login/LoginViewModel.kt
package com.konkuk.moru.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.core.datastore.OnboardingPreference
import com.konkuk.moru.data.repositoryimpl.AuthRepository as ImplAuthRepository
import com.konkuk.moru.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    // [변경] 구현체 주입 (Hilt 바인딩이 이미 되어 있다면 별도 수정 불필요)
    private val authRepository: ImplAuthRepository, // [변경]
    private val userRepository: UserRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    // [변경] onSuccess 시그니처 확장: 서버의 온보딩 완료 여부 전달
    fun login(
        email: String,
        password: String,
        context: Context,
        onSuccess: (Boolean) -> Unit, // [변경]
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            runCatching {
                // [변경] 서버 로그인 + 토큰 저장 + isOnboarding 수신
                val resp = authRepository.loginAndSaveTokens(context, email, password)

                // [변경] 서버 플래그를 로컬 OnboardingPreference에 반영
                if (resp.isOnboarding) {
                    // 서버가 isOnboarding=true를 "온보딩 완료" 의미로 준다고 가정
                    // (반대 의미라면 여기만 false 분기 바꾸면 됨)
                    OnboardingPreference.setOnboardingComplete(context)
                }

                // [유지] LoginPreference에도 로그인 상태/토큰 저장 (TokenPreference와 중복 저장되어도 무해)
                LoginPreference.setLoggedIn(context, true)
                LoginPreference.saveAccessToken(context, resp.token.accessToken)
                LoginPreference.saveRefreshToken(context, resp.token.refreshToken)

                userSessionManager.setLoggedIn(true)
                _isLoggedIn.value = true

                // 사용자 정보 로드는 실패해도 로그인 흐름은 유지
                runCatching { userRepository.getUserProfile() }
                    .onSuccess { profile -> userSessionManager.setUserProfile(profile) }

                // [변경] 콜백에 '온보딩 완료 여부' 전달
                onSuccess(resp.isOnboarding)
            }.onFailure { e ->
                onFailure(e.message ?: "로그인 실패")
            }
        }
    }
}