package com.konkuk.moru.presentation.signup

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val traceId = UUID.randomUUID().toString().take(8)
            val t0 = SystemClock.elapsedRealtime()
            Log.d("signup", "[$traceId] start: email=$email")

            val result = authRepository.signUp(email, password)
            val ms = SystemClock.elapsedRealtime() - t0

            result.fold(
                onSuccess = {
                    Log.d("signup", "[$traceId] success in ${ms}ms")
                    onSuccess()
                },
                onFailure = { e ->
                    Log.e("signup", "[$traceId] fail in ${ms}ms: ${e.message}", e)
                    onFailure(e.message ?: "회원가입에 실패했습니다. 다시 시도해주세요.")
                }
            )
        }
    }
}