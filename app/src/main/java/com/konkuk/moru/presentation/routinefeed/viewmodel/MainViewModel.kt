package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _hasUnreadNotification = MutableStateFlow(false)
    val hasUnreadNotification = _hasUnreadNotification.asStateFlow()

    init {
        fetchUnreadCount()
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            runCatching { notificationRepository.getUnreadCount() > 0 }
                .onSuccess { hasUnread -> _hasUnreadNotification.value = hasUnread }
        }
    }

    fun onNotificationIconClicked() {
        if (!_hasUnreadNotification.value) return
        _hasUnreadNotification.value = false // UI 즉시 업데이트
        viewModelScope.launch {
            // 나중에 서버 API가 추가되면 이 호출이 실제로 동작합니다.
            notificationRepository.markAllAsRead()
        }
    }
}