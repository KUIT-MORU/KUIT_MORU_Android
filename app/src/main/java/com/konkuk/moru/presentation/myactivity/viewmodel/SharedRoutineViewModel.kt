package com.konkuk.moru.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import com.konkuk.moru.presentation.home.FocusType
import com.konkuk.moru.presentation.home.RoutineStepData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalTime

class SharedRoutineViewModel : ViewModel() {
    private val _routineTitle = MutableStateFlow("")
    val routineTitle: StateFlow<String> = _routineTitle
    fun setRoutineTitle(title: String) {
        _routineTitle.value = title
    }

    private val _routineCategory = MutableStateFlow("")
    val routineCategory: StateFlow<String> = _routineCategory

    private val _routineTags = MutableStateFlow<List<String>>(emptyList())
    val routineTags: StateFlow<List<String>> = _routineTags
    fun setRoutineTags(tags: List<String>) {
        _routineTags.value = tags
    }

    fun setRoutineInfo(title: String, category: String, tags: List<String>) {
        _routineTitle.value = title
        _routineCategory.value = category
        _routineTags.value = tags
    }

    private val _focusType = MutableStateFlow(FocusType.FOCUS)
    val focusType: StateFlow<FocusType> = _focusType

    private val _scheduledTime = MutableStateFlow<LocalTime?>(null)
    val scheduledTime: StateFlow<LocalTime?> = _scheduledTime

    private val _scheduledDays = MutableStateFlow<Set<DayOfWeek>>(emptySet())
    val scheduledDays: StateFlow<Set<DayOfWeek>> = _scheduledDays

    fun setSchedule(time: LocalTime?, days: Set<DayOfWeek>) {
        _scheduledTime.value = time
        _scheduledDays.value = days
    }

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