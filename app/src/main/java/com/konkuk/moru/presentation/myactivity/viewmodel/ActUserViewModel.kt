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
class ActUserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _nickname = MutableStateFlow<String?>(null)
    val nickname: StateFlow<String?> = _nickname

    private val _routineCount = MutableStateFlow(0)
    val routineCount: StateFlow<Int> = _routineCount

    private val _followerCount = MutableStateFlow(0)
    val followerCount: StateFlow<Int> = _followerCount

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount


    fun loadMe() = viewModelScope.launch {
        runCatching { userRepository.getUserProfile() }
            .onSuccess { profile ->
                _nickname.value = profile.nickname
                _routineCount.value = profile.routineCount
                _followerCount.value = profile.followerCount
                _followingCount.value = profile.followingCount
            }
            .onFailure { e -> Log.e("ActUserViewModel", "getUserProfile failed", e) }
    }
}