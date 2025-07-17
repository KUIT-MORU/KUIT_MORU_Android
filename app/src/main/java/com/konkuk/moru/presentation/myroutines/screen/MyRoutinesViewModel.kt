package com.konkuk.moru.presentation.myroutines.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
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

    // [수정] 원본 데이터 타입을 MyRoutine에서 통합 Routine으로 변경
    private val _sourceRoutines = MutableStateFlow<List<Routine>>(emptyList())

    private val _uiState = MutableStateFlow(MyRoutinesUiState())
    val uiState: StateFlow<MyRoutinesUiState> = _uiState.asStateFlow()

    // [수정] 화면 표시용 데이터 타입도 통합 Routine으로 변경
    val routinesToDisplay: StateFlow<List<Routine>> = combine(
        _sourceRoutines,
        uiState
    ) { routines, state ->
        val filteredByDay = if (state.selectedDay == null) {
            routines
        } else {
            routines.filter { it.scheduledDays.contains(state.selectedDay) }
        }
        when (state.selectedSortOption) {
            SortOption.BY_TIME -> filteredByDay.sortedBy { it.scheduledTime ?: LocalTime.MAX }
            SortOption.LATEST -> filteredByDay.sortedByDescending { it.routineId }
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
            currentList.map { if (it.routineId == routineId) it.copy(isChecked = isChecked) else it }
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
        _sourceRoutines.update { currentList ->
            currentList.filterNot { it.isChecked }
        }
        _uiState.update {
            it.copy(
                isDeleteMode = false,
                showDeleteDialog = false,
                showDeleteSuccessDialog = true
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
                // [수정] it.copy에 isAlarmEnabled = alarm 추가
                if (it.routineId == routineId) it.copy(
                    scheduledTime = time,
                    scheduledDays = days,
                    isAlarmEnabled = alarm // ◀◀◀ [수정] 전달받은 alarm 값을 저장
                ) else it
            }
        }
        closeTimePicker()
    }

    fun onLikeClick(routineId: Int) {
        _sourceRoutines.update { currentList ->
            currentList.map { routine ->
                if (routine.routineId == routineId) {
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

    fun onShowInfoTooltip() {
        _uiState.update { it.copy(showInfoTooltip = true) }
    }

    private fun uncheckAllRoutines() {
        _sourceRoutines.update { currentList ->
            currentList.map { it.copy(isChecked = false) }
        }
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            // [수정] 샘플 데이터를 새로운 통합 Routine 클래스로 변경
            _sourceRoutines.value = DummyData.dummyRoutines
        }
    }
}