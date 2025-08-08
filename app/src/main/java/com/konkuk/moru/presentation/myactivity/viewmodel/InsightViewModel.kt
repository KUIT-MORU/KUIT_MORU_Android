package com.konkuk.moru.presentation.myactivity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.domain.repository.InsightRepository
import com.konkuk.moru.presentation.login.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightViewModel @Inject constructor(
    private val insightRepository: InsightRepository,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _paceGrade = MutableStateFlow("로딩 중...")
    val paceGrade: StateFlow<String> = _paceGrade

    init {
        viewModelScope.launch {
            userSessionManager.isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    loadInsights()
                }
            }
        }
    }

    private fun loadInsights() {
        viewModelScope.launch {
            insightRepository.getInsights()
                .onSuccess {
                    _paceGrade.value = it.paceGrade
                }
                .onFailure {
                    _paceGrade.value = "불러오기 실패: ${it.message}"
                }
        }
    }
}
