package com.konkuk.moru.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import com.konkuk.moru.presentation.home.FocusType
import com.konkuk.moru.presentation.home.RoutineStepData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedRoutineViewModel : ViewModel() {
    private val _focusType = MutableStateFlow(FocusType.FOCUS)
    val focusType: StateFlow<FocusType> = _focusType

    private val _startNavigation = MutableStateFlow<FocusType?>(null)
    val startNavigation: StateFlow<FocusType?> = _startNavigation

    fun setFocusType(focusType: FocusType) {
        _focusType.value = focusType
    }

    fun onStartClick() {
        _startNavigation.value = _focusType.value
    }

    fun onNavigationHandled() {
        _startNavigation.value = null
    }

    //선택된 스텝 보관하는 함수 추가
    private val _selectedSteps = MutableStateFlow<List<RoutineStepData>>(emptyList())
    val selectedSteps: StateFlow<List<RoutineStepData>> = _selectedSteps

    fun setSelectedSteps(steps: List<RoutineStepData>) {
        _selectedSteps.value = steps
    }

}