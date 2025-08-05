package com.konkuk.moru.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import com.konkuk.moru.presentation.home.RoutineStepData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalTime

class SharedRoutineViewModel : ViewModel() {

    // 루틴 제목
    private val _routineTitle = MutableStateFlow("")
    val routineTitle: StateFlow<String> = _routineTitle
    fun setRoutineTitle(title: String) {
        _routineTitle.value = title
    }

    // 루틴 카테고리 (집중 / 간편)
    private val _routineCategory = MutableStateFlow("")
    val routineCategory: StateFlow<String> = _routineCategory
    fun setRoutineCategory(category: String) {
        _routineCategory.value = category
    }

    // 루틴 태그 리스트
    private val _routineTags = MutableStateFlow<List<String>>(emptyList())
    val routineTags: StateFlow<List<String>> = _routineTags
    fun setRoutineTags(tags: List<String>) {
        _routineTags.value = tags
    }

    // 제목, 카테고리, 태그 한꺼번에 설정
    fun setRoutineInfo(title: String, category: String, tags: List<String>) {
        _routineTitle.value = title
        _routineCategory.value = category
        _routineTags.value = tags
    }

    // 알림 시간 및 요일
    private val _scheduledTime = MutableStateFlow<LocalTime?>(null)
    val scheduledTime: StateFlow<LocalTime?> = _scheduledTime

    private val _scheduledDays = MutableStateFlow<Set<DayOfWeek>>(emptySet())
    val scheduledDays: StateFlow<Set<DayOfWeek>> = _scheduledDays

    fun setSchedule(time: LocalTime?, days: Set<DayOfWeek>) {
        _scheduledTime.value = time
        _scheduledDays.value = days
    }

    // 네비게이션 트리거 (카테고리 기반으로 변경)
    private val _startNavigation = MutableStateFlow<String?>(null)
    val startNavigation: StateFlow<String?> = _startNavigation

    fun onStartClick() {
        _startNavigation.value = _routineCategory.value
    }

    fun onNavigationHandled() {
        _startNavigation.value = null
    }

    // 선택된 스텝 리스트
    private val _selectedSteps = MutableStateFlow<List<RoutineStepData>>(emptyList())
    val selectedSteps: StateFlow<List<RoutineStepData>> = _selectedSteps

    fun setSelectedSteps(steps: List<RoutineStepData>) {
        _selectedSteps.value = steps
    }
}
