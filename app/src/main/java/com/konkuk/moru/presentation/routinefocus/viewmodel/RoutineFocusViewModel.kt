package com.konkuk.moru.presentation.routinefocus.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.presentation.routinefocus.screen.parseTimeToSeconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RoutineFocusViewModel : ViewModel() {
    // 시간이 흐르고 있는지 유무
    var isTimerRunning by mutableStateOf(false)
        private set

    // 시간 초과 상태인지
    var isTimeout by mutableStateOf(false)
        private set

    // 현재 진행 중인 스탭이 어디인지
    var currentStep by mutableIntStateOf(1)
        private set

    // 각 스탭의 현재 경과 시간
    var elapsedSeconds by mutableIntStateOf(0)
        private set

    // 총 경과 시간
    var totalElapsedSeconds by mutableIntStateOf(0)
        private set

    // 가로 세로 모드를 판단
    var isLandscapeMode by mutableStateOf(false)
        private set

    // 가로 모드 토글 함수
    fun toggleLandscapeMode() {
        isLandscapeMode = !isLandscapeMode
    }

    fun setLandscapeModeOn() {
        isLandscapeMode = true
    }

    fun setLandscapeModeOff() {
        isLandscapeMode = false
    }

    // 다크 모드 판단
    var isDarkMode by mutableStateOf(false)
        private set

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }

    fun setDarkModeOn() {
        isDarkMode = true
    }

    fun setDarkModeOff() {
        isDarkMode = false
    }

    // 정지/재생 버튼 상태 저장
    private var stepLimit = 0

    fun setStepLimitFromTimeString(limitInSeconds: Int) {
        stepLimit = limitInSeconds
    }

    var isUserPaused by mutableStateOf(false)
        private set

    fun togglePause() {
        isUserPaused = !isUserPaused
        if (isUserPaused) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    // 설정 팝업 상태 저장
    var isSettingsPopupVisible by mutableStateOf(false)
        private set

    fun toggleSettingsPopup() {
        isSettingsPopupVisible = !isSettingsPopupVisible
    }

    fun closeSettingsPopup() {
        isSettingsPopupVisible = false
    }

    // 사용 앱 팝업 상태 저장
    var isAppIconsVisible by mutableStateOf(false)
        private set

    fun toggleAppIcons() {
        isAppIconsVisible = !isAppIconsVisible
    }

    // 타이머 시작 함수
    fun startTimer() {
        if (isTimerRunning) return
        isTimerRunning = true
        isTimeout = false
        viewModelScope.launch {
            while (isTimerRunning) {
                delay(1000)
                elapsedSeconds++
                if (!isTimeout && elapsedSeconds > stepLimit) {
                    isTimeout = true
                }
            }
        }
    }


    // 타이머 일시정지 함수
    fun pauseTimer() {
        isTimerRunning = false
    }


    // 재생/정지 버튼 재개 함수
    fun resumeTimer() {
        if (!isTimerRunning) {
            isUserPaused = false
            startTimer()
        }
    }


    // 다음 스텝으로 넘어갈 때 호출하는 함수
    fun nextStep(newTimeString: String) {
        totalElapsedSeconds += elapsedSeconds
        elapsedSeconds = 0
        isTimeout = false
        currentStep++
        setStepLimitFromTimeString(parseTimeToSeconds(newTimeString))
    }

    // 현재 스텝의 시간을 리셋할 때 호출하는 함수
    fun resetTimer() {
        elapsedSeconds = 0
        isTimeout = false
    }
}