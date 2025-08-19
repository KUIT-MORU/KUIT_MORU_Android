package com.konkuk.moru.presentation.routinefocus.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.presentation.routinefeed.data.AppDto
import com.konkuk.moru.core.util.HomeAppUtils
import com.konkuk.moru.presentation.routinefocus.screen.parseTimeToSeconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RoutineFocusViewModel : ViewModel() {

    // ViewModel에 추가해야 할 메서드들
    fun getMemoText(stepNumber: Int): String {
        return _stepMemos.value[stepNumber] ?: ""
    }


    // 시간이 흐르고 있는지 유무
    var isTimerRunning by mutableStateOf(false)
        private set

    // 시간 초과 상태인지
    var isTimeout by mutableStateOf(false)
        private set

    // 현재 진행 중인 스탭이 어디인지
    var currentStep by mutableIntStateOf(1)
        private set

    // 현재 스텝 설정 함수
    fun updateCurrentStep(step: Int) {
        currentStep = step
    }

    // 각 스탭의 현재 경과 시간
    var elapsedSeconds by mutableIntStateOf(0)
        private set

    // 총 경과 시간
    var totalElapsedSeconds by mutableIntStateOf(0)
        private set

    // 가로 세로 모드를 판단
    var isLandscapeMode by mutableStateOf(false)
        private set

    fun updateMemoText(stepNumber: Int, text: String) {
        _stepMemos.value = _stepMemos.value.toMutableMap().apply {
            put(stepNumber, text)
        }
    }

    // 가로 모드 토글 함수
    fun toggleLandscapeMode() {
        Log.d("RoutineFocusViewModel", "🔄 가로모드 토글: ${if (isLandscapeMode) "가로" else "세로"} → ${if (!isLandscapeMode) "가로" else "세로"}")
        Log.d("RoutineFocusViewModel", "📱 토글 전 팝업 상태 - showAppIcons: $showAppIcons, showMemoPad: $showMemoPad")
        Log.d("RoutineFocusViewModel", "📝 토글 전 메모 상태: $_stepMemos")
        
        isLandscapeMode = !isLandscapeMode
        
        Log.d("RoutineFocusViewModel", "📱 토글 후 팝업 상태 - showAppIcons: $showAppIcons, showMemoPad: $showMemoPad")
        Log.d("RoutineFocusViewModel", "📝 토글 후 메모 상태: $_stepMemos")
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

    // 화면 차단 오버레이 관련
    var isScreenBlockOverlayVisible by mutableStateOf(false)
    private val _selectedApps = mutableStateOf<List<AppDto>>(emptyList())
    val selectedApps: List<AppDto>
        get() = _selectedApps.value
    
    fun showScreenBlockOverlay(apps: List<AppDto>) {
        Log.d("RoutineFocusViewModel", "🛡️ showScreenBlockOverlay 호출: apps.size=${apps.size}")
        _selectedApps.value = apps
        isScreenBlockOverlayVisible = true
        Log.d("RoutineFocusViewModel", "🛡️ isScreenBlockOverlayVisible = $isScreenBlockOverlayVisible")
    }
    
    fun hideScreenBlockOverlay() {
        Log.d("RoutineFocusViewModel", "🛡️ hideScreenBlockOverlay 호출")
        isScreenBlockOverlayVisible = false
        Log.d("RoutineFocusViewModel", "🛡️ isScreenBlockOverlayVisible = $isScreenBlockOverlayVisible")
    }
    
    fun setSelectedApps(apps: List<AppDto>, context: Context) {
        Log.d("RoutineFocusViewModel", "🔄 setSelectedApps 호출됨")
        Log.d("RoutineFocusViewModel", "📱 전달받은 앱 개수: ${apps.size}")
        Log.d("RoutineFocusViewModel", "📱 앱 상세 정보:")
        apps.forEachIndexed { index, app ->
            Log.d("RoutineFocusViewModel", "   ${index + 1}. 이름: ${app.name}, 패키지: ${app.packageName}")
        }
        
        // 서버에서 받은 앱 목록을 필터링하여 실행 가능한 앱만 사용
        val filteredApps = apps.filter { app ->
            try {
                // LAUNCHER 액티비티가 있는 앱만 포함
                val packageManager = context.packageManager
                packageManager.getLaunchIntentForPackage(app.packageName) != null
            } catch (e: Exception) {
                Log.w("RoutineFocusViewModel", "⚠️ 앱 필터링 중 오류: ${app.packageName}", e)
                false
            }
        }
        
        Log.d("RoutineFocusViewModel", "🔍 필터링 결과: ${apps.size}개 → ${filteredApps.size}개")
        filteredApps.forEachIndexed { index, app ->
            Log.d("RoutineFocusViewModel", "   ✅ 실행 가능한 앱 ${index + 1}: ${app.name} (${app.packageName})")
        }
        
        _selectedApps.value = filteredApps
        Log.d("RoutineFocusViewModel", "✅ selectedApps 설정 완료: ${_selectedApps.value.size}개")
        
        // 추가 로그: 업데이트 후 상태 확인
        Log.d("RoutineFocusViewModel", "🔍 업데이트 후 selectedApps 확인: ${_selectedApps.value.size}개")
        _selectedApps.value.forEachIndexed { index, app ->
            Log.d("RoutineFocusViewModel", "   - 업데이트 후 앱 ${index + 1}: ${app.name} (${app.packageName})")
        }
    }
    
    /**
     * 실제 설치된 앱 목록을 가져와서 설정합니다.
     * 선택된 앱이 없거나 비어있을 때 사용됩니다.
     */
    fun loadInstalledApps(context: Context) {
        Log.d("RoutineFocusViewModel", "🔄 loadInstalledApps 호출됨")
        
        viewModelScope.launch {
            try {
                // 사용자 설치 앱 가져오기 (시스템 앱 제외)
                val userApps = HomeAppUtils.getInstalledUserApps(context)
                Log.d("RoutineFocusViewModel", "📱 사용자 설치 앱 ${userApps.size}개 로드 완료")
                
                if (userApps.isNotEmpty()) {
                    // 처음 20개만 사용 (너무 많으면 성능 문제)
                    _selectedApps.value = userApps.take(20)
                    Log.d("RoutineFocusViewModel", "✅ 사용자 설치 앱으로 selectedApps 설정 완료 (처음 20개)")
                } else {
                    // 사용자 앱이 없으면 모든 앱 가져오기 (시스템 앱 포함)
                    val allApps = HomeAppUtils.getAllInstalledApps(context)
                    Log.d("RoutineFocusViewModel", "📱 모든 앱 ${allApps.size}개 로드 완료")
                    
                    if (allApps.isNotEmpty()) {
                        _selectedApps.value = allApps.take(20)
                        Log.d("RoutineFocusViewModel", "✅ 모든 앱으로 selectedApps 설정 완료 (처음 20개)")
                    }
                }
                
                Log.d("RoutineFocusViewModel", "🎯 최종 selectedApps: ${_selectedApps.value.size}개")
                _selectedApps.value.forEachIndexed { index, app ->
                    Log.d("RoutineFocusViewModel", "   ${index + 1}. ${app.name} (${app.packageName})")
                }
                
            } catch (e: Exception) {
                Log.e("RoutineFocusViewModel", "❌ 설치된 앱 로드 실패", e)
                // 실패 시 빈 리스트로 설정
                _selectedApps.value = emptyList()
            }
        }
    }
    
    // 기존 팝업 관련 (하위 호환성 유지)
    var isScreenBlockPopupVisible by mutableStateOf(false)
    
    fun showScreenBlockPopup(apps: List<AppDto>) {
        _selectedApps.value = apps
        isScreenBlockPopupVisible = true
    }
    
    fun hideScreenBlockPopup() {
        isScreenBlockPopupVisible = false
    }
    
    // 온보딩 팝업창 관련
    var isOnboardingPopupVisible by mutableStateOf(false)
    
    fun showOnboardingPopup() {
        isOnboardingPopupVisible = true
    }
    
    fun hideOnboardingPopup() {
        isOnboardingPopupVisible = false
    }



    // 집중 루틴 활성화 상태
    var _isFocusRoutineActive by mutableStateOf(false)
        private set

    // 허용된 앱 실행 플래그
    var _isPermittedAppLaunch by mutableStateOf(false)
        private set

    fun startFocusRoutine() {
        android.util.Log.d("RoutineFocusViewModel", "🚀 startFocusRoutine 호출됨!")
        _isFocusRoutineActive = true
        android.util.Log.d("RoutineFocusViewModel", "✅ _isFocusRoutineActive = $_isFocusRoutineActive")
    }

    fun endFocusRoutine() {
        _isFocusRoutineActive = false
        _isPermittedAppLaunch = false
        
        // 루틴 종료 시 모든 상태 초기화
        isTimerRunning = false
        isTimeout = false
        currentStep = 1
        elapsedSeconds = 0
        totalElapsedSeconds = 0
        isUserPaused = false
        stepLimit = 0
        
        // 팝업 상태들도 초기화
        isAppIconsVisible = false
        showMemoPad = false
        isScreenBlockOverlayVisible = false
        isScreenBlockPopupVisible = false
        isOnboardingPopupVisible = false
        isSettingsPopupVisible = false
        
        // 선택된 앱들 초기화
        _selectedApps.value = emptyList()
        
        // 스텝별 메모 초기화
        _stepMemos.value = emptyMap()
        
        Log.d("RoutineFocusViewModel", "🔄 루틴 종료: 모든 상태 초기화 완료")
    }

    fun setPermittedAppLaunch(permitted: Boolean) {
        _isPermittedAppLaunch = permitted
    }

    // 외부에서 읽기 전용으로 접근할 수 있는 프로퍼티
    val isPermittedAppLaunch: Boolean
        get() = _isPermittedAppLaunch

    val isFocusRoutineActive: Boolean
        get() = _isFocusRoutineActive

    // 사용 앱 팝업 상태 저장
    var isAppIconsVisible by mutableStateOf(false)
        private set

    // 세로모드와의 호환성을 위한 별칭
    val showAppIcons: Boolean
        get() = isAppIconsVisible

    fun toggleAppIcons() {
        // 강제 테스트 로그
        android.util.Log.e("TEST_LOG", "🔥 toggleAppIcons 호출됨! 현재 상태: $isAppIconsVisible")
        System.out.println("🔥 System.out: toggleAppIcons 호출됨! 현재 상태: $isAppIconsVisible")
        
        android.util.Log.d("RoutineFocusViewModel", "📱 앱 아이콘 팝업 토글: $isAppIconsVisible → ${!isAppIconsVisible}")
        isAppIconsVisible = !isAppIconsVisible
        android.util.Log.d("RoutineFocusViewModel", "✅ isAppIconsVisible = $isAppIconsVisible")
    }

    fun hideAppIcons() {
        isAppIconsVisible = false
    }

    // 메모장 팝업 상태 저장
    var showMemoPad by mutableStateOf(false)
        private set

    // 스텝별 메모 저장
    private val _stepMemos = MutableStateFlow<Map<Int, String>>(emptyMap())
    val stepMemos: StateFlow<Map<Int, String>> = _stepMemos.asStateFlow()

    fun toggleMemoPad() {
        Log.d("RoutineFocusViewModel", "📝 메모장 팝업 토글: $showMemoPad → ${!showMemoPad}")
        showMemoPad = !showMemoPad
    }

    fun hideMemoPad() {
        showMemoPad = false
    }

    // 특정 스텝의 메모 저장
    fun saveStepMemo(step: Int, memo: String) {
        Log.d("RoutineFocusViewModel", "📝 saveStepMemo 호출: step=$step, memo='$memo'")
        Log.d("RoutineFocusViewModel", "📝 저장 전 메모 상태: ${_stepMemos.value}")
        
        _stepMemos.value = _stepMemos.value.toMutableMap().apply {
            put(step, memo)
        }
        
        Log.d("RoutineFocusViewModel", "📝 저장 후 메모 상태: ${_stepMemos.value}")
        Log.d("RoutineFocusViewModel", "📝 스텝 $step 메모 저장 완료: $memo")
    }

    // 특정 스텝의 메모 가져오기
    fun getStepMemo(step: Int): String {
        val memo = _stepMemos.value[step] ?: ""
        Log.d("RoutineFocusViewModel", "📖 스텝 $step 메모 불러오기: $memo")
        return memo
    }
    
    // 특정 스텝의 메모를 StateFlow로 제공
    fun getStepMemoFlow(step: Int): StateFlow<String> {
        return MutableStateFlow(_stepMemos.value[step] ?: "").asStateFlow()
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

    // 타이머 재개 함수
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