package com.konkuk.moru.presentation.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.core.datastore.LoginPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    // 이후 서버 회원가입 API 연결할 때 사용
    fun signUp(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            // TODO: 실제 API 호출해서 가입 요청
            val isMockSuccess = true // 임시

            if (isMockSuccess) {
                LoginPreference.setLoggedIn(context, true)
                onSuccess()
            } else {
                onFailure("회원가입에 실패했습니다. 다시 시도해주세요.")
            }
        }
    }
}