package com.konkuk.moru.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 도메인 모델
data class UserMe(val id: String, val nickname: String)

// 리포지토리 인터페이스 예시 (이미 있다면 그 인터페이스 주입)
interface UserRepository {
    suspend fun getMe(): UserMe
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname

    fun loadMe() = viewModelScope.launch {
        runCatching { userRepository.getMe() }
            .onSuccess { me -> _nickname.value = me.nickname }
            .onFailure { e -> Log.e("UserViewModel", "getMe failed", e) }
    }
}
