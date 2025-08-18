package com.konkuk.moru.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname

    fun loadMe() = viewModelScope.launch {
        Log.d("UserViewModel", "🔄 loadMe() 호출됨")
        runCatching { 
            Log.d("UserViewModel", "🔗 getUserProfile API 호출 중...")
            userRepository.getUserProfile() 
        }
            .onSuccess { profile -> 
                Log.d("UserViewModel", "✅ getUserProfile 성공!")
                Log.d("UserViewModel", "   - 닉네임: ${profile.nickname}")
                Log.d("UserViewModel", "   - 이메일: ${profile.id}")
                Log.d("UserViewModel", "   - 루틴 수: ${profile.routineCount}")
                _nickname.value = profile.nickname 
            }
            .onFailure { e -> 
                Log.e("UserViewModel", "❌ getUserProfile 실패!", e)
                Log.e("UserViewModel", "🔍 예외 타입: ${e.javaClass.simpleName}")
                Log.e("UserViewModel", "🔍 예외 메시지: ${e.message}")
            }
        }
}
