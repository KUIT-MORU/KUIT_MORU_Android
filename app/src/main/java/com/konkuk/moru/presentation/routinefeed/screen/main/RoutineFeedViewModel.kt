package com.konkuk.moru.presentation.routinefeed.screen.main


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// RoutineFeedScreen의 UI 상태를 나타내는 데이터 클래스
data class RoutineFeedUiState(
    val hasNotification: Boolean = true // 기본적으로는 알림이 있는 상태로 시작
)

class RoutineFeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RoutineFeedUiState())
    val uiState = _uiState.asStateFlow()

    // 알림 아이콘을 클릭했을 때 호출되는 함수
    fun onNotificationViewed() {
        // hasNotification 상태를 false로 변경
        _uiState.update { currentState ->
            currentState.copy(hasNotification = false)
        }
    }
}