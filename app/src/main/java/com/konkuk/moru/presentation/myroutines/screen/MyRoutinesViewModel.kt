package com.konkuk.moru.presentation.myroutines.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime


class MyRoutinesViewModel : ViewModel() {

    // 1. 서버에서 가져온 원본 데이터 (Private)
    private val _sourceRoutines = MutableStateFlow<List<MyRoutine>>(emptyList())

    // 2. UI의 상태 (선택된 필터, 정렬 옵션 등)
    private val _uiState = MutableStateFlow(MyRoutinesUiState())
    val uiState: StateFlow<MyRoutinesUiState> = _uiState.asStateFlow()

    // 3. 최종적으로 화면에 표시될 데이터 (필터링과 정렬이 적용된 결과)
    val routinesToDisplay: StateFlow<List<MyRoutine>> = combine(
        _sourceRoutines,
        uiState
    ) { routines, state ->
        //요일 필터링
        val filteredByDay = if (state.selectedDay == null) {
            routines
        } else {
            routines.filter { it.scheduledDays.contains(state.selectedDay) }
        }
        // 정렬 옵션 적용
        when (state.selectedSortOption) {
            SortOption.BY_TIME -> filteredByDay.sortedBy { it.scheduledTime ?: LocalTime.MAX }
            SortOption.LATEST -> filteredByDay.sortedByDescending { it.id }
            SortOption.POPULAR -> filteredByDay.sortedByDescending { it.likes }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadRoutines()
    }

    // --- 이벤트 핸들러 ---

    fun onSortOptionSelected(option: SortOption) {
        _uiState.update { it.copy(selectedSortOption = option) }
    }

    fun onDaySelected(day: DayOfWeek?) {
        val newSelectedDay = if (_uiState.value.selectedDay == day) null else day
        _uiState.update { it.copy(selectedDay = newSelectedDay) }
    }

    fun onTrashClick() {
        val currentMode = _uiState.value.isDeleteMode
        _uiState.update { it.copy(isDeleteMode = !currentMode) }
        if (currentMode) {
            uncheckAllRoutines()
        }
    }

    fun onDismissInfoTooltip() {
        _uiState.update { it.copy(showInfoTooltip = false) }
    }

    fun onCheckRoutine(routineId: Int, isChecked: Boolean) {
        _sourceRoutines.update { currentList ->
            currentList.map { if (it.id == routineId) it.copy(isChecked = isChecked) else it }
        }
    }

    fun showDeleteDialog() {
        if (routinesToDisplay.value.any { it.isChecked }) {
            _uiState.update { it.copy(showDeleteDialog = true) }
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun dismissDeleteSuccessDialog() {
        _uiState.update { it.copy(showDeleteSuccessDialog = false) }
    }

    fun deleteCheckedRoutines() {
        // 실제 데이터 삭제 로직
        _sourceRoutines.update { currentList ->
            currentList.filterNot { it.isChecked }
        }
        // UI 상태 업데이트
        _uiState.update {
            it.copy(
                isDeleteMode = false, // 삭제 모드 종료
                showDeleteDialog = false, // 삭제 '확인' 모달 닫기
                showDeleteSuccessDialog = true // ✨ 삭제 '성공' 모달 띄우기
            )
        }
    }

    fun openTimePicker(routineId: Int) {
        _uiState.update { it.copy(editingRoutineId = routineId) }
    }

    fun closeTimePicker() {
        _uiState.update { it.copy(editingRoutineId = null) }
    }

    fun onConfirmTimeSet(routineId: Int, time: LocalTime, days: Set<DayOfWeek>, alarm: Boolean) {
        _sourceRoutines.update { currentList ->
            currentList.map {
                if (it.id == routineId) it.copy(scheduledTime = time, scheduledDays = days) else it
            }
        }
        closeTimePicker()
    }

    // ✅ [추가] 좋아요 클릭 이벤트 핸들러
    fun onLikeClick(routineId: Int) {
        _sourceRoutines.update { currentList ->
            currentList.map { routine ->
                if (routine.id == routineId) {
                    routine.copy(
                        isLiked = !routine.isLiked,
                        likes = if (routine.isLiked) routine.likes - 1 else routine.likes + 1
                    )
                } else {
                    routine
                }
            }
        }
    }

    // ✅ [추가] 정보 아이콘 클릭 이벤트 핸들러
    fun onShowInfoTooltip() {
        // 현재 구조에서는 Screen이 툴팁 표시를 직접 관리하므로
        // ViewModel에서는 로그를 남기거나 비워두어도 괜찮습니다.
        // 또는 UiState에 showInfoTooltip 변수를 추가하여 관리할 수도 있습니다.
        _uiState.update { it.copy(showInfoTooltip = true) }
    }

    private fun uncheckAllRoutines() {
        _sourceRoutines.update { currentList ->
            currentList.map { it.copy(isChecked = false) }
        }
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            _sourceRoutines.value = listOf(
                MyRoutine(
                    id = 1,
                    name = "아침 운동",
                    tags = listOf("#모닝루틴", "#스트레칭"),
                    likes = 16,
                    isLiked = true,
                    isRunning = false,
                    scheduledTime = LocalTime.of(7, 30),
                    scheduledDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
                ),
                MyRoutine(
                    id = 2,
                    name = "오전 명상",
                    tags = listOf("#마음챙김", "#집중"),
                    likes = 25,
                    isLiked = false,
                    isRunning = true,
                    scheduledTime = LocalTime.of(9, 0),
                    scheduledDays = setOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY
                    )
                ),
                MyRoutine(
                    id = 3,
                    name = "점심 후 산책",
                    tags = listOf("#건강", "#소화"),
                    likes = 112,
                    isLiked = true,
                    isRunning = false,
                    scheduledTime = LocalTime.of(13, 0),
                    scheduledDays = DayOfWeek.values().toSet()
                ),
                MyRoutine(
                    id = 4,
                    name = "영어 공부",
                    tags = listOf("#자기계발", "#외국어"),
                    likes = 42,
                    isLiked = false,
                    isRunning = true,
                    scheduledTime = LocalTime.of(21, 0),
                    scheduledDays = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
                ),
                MyRoutine(
                    id = 5,
                    name = "일기 쓰기",
                    tags = listOf("#회고", "#감사"),
                    likes = 33,
                    isLiked = false,
                    isRunning = false,
                    scheduledTime = null,
                    scheduledDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                )
            )
        }
    }
}