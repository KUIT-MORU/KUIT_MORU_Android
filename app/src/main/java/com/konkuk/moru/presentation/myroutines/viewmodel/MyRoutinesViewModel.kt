package com.konkuk.moru.presentation.myroutines.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineUi
import com.konkuk.moru.domain.repository.MyRoutineRepository
import com.konkuk.moru.presentation.myroutines.screen.MyRoutinesUiState
import com.konkuk.moru.presentation.myroutines.screen.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class MyRoutinesViewModel @Inject constructor(
    private val repo: MyRoutineRepository
) : ViewModel() {

    private val _sourceRoutines = MutableStateFlow<List<MyRoutineUi>>(emptyList())
    val routinesToDisplay: StateFlow<List<MyRoutineUi>> =
        _sourceRoutines.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(MyRoutinesUiState())
    val uiState: StateFlow<MyRoutinesUiState> = _uiState.asStateFlow()

    init {
        refreshRoutines()
        viewModelScope.launch {
            uiState
                .map { it.selectedSortOption to it.selectedDay }
                .distinctUntilChanged()
                .collect { refreshRoutines() }
        }
    }

    fun onSortOptionSelected(option: SortOption) {
        // ✅ 요일 선택값은 건드리지 않음 (정렬과 독립)
        _uiState.update { it.copy(selectedSortOption = option) }
    }

    fun onDaySelected(day: DayOfWeek?) {
        val newSelectedDay = if (_uiState.value.selectedDay == day) null else day
        _uiState.update { it.copy(selectedDay = newSelectedDay) }
    }

    fun onTrashClick() {
        val currentMode = _uiState.value.isDeleteMode
        _uiState.update { it.copy(isDeleteMode = !currentMode) }
        if (currentMode) uncheckAllRoutines()
    }

    fun onDismissInfoTooltip() {
        _uiState.update { it.copy(showInfoTooltip = false) }
    }

    fun onCheckRoutine(routineId: String, isChecked: Boolean) {
        _sourceRoutines.update { list -> list.map { if (it.routineId == routineId) it.copy(isChecked = isChecked) else it } }
    }

    fun showDeleteDialog() {
        if (routinesToDisplay.value.any { it.isChecked }) _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun dismissDeleteSuccessDialog() {
        _uiState.update { it.copy(showDeleteSuccessDialog = false) }
    }

    fun deleteCheckedRoutines() {
        viewModelScope.launch {
            val targets = _sourceRoutines.value.filter { it.isChecked }.map { it.routineId }
            targets.forEach { id -> repo.deleteRoutine(id) }
            _uiState.update {
                it.copy(
                    isDeleteMode = false,
                    showDeleteDialog = false,
                    showDeleteSuccessDialog = true
                )
            }
            refreshRoutines()
        }
    }

    fun openTimePicker(routineId: String) {
        _uiState.update { it.copy(editingRoutineId = routineId) }
    }

    fun closeTimePicker() {
        _uiState.update { it.copy(editingRoutineId = null) }
    }

    fun onConfirmTimeSet(routineId: String, time: LocalTime, days: Set<DayOfWeek>, alarm: Boolean) {
        _sourceRoutines.update { list ->
            list.map {
                if (it.routineId == routineId) it.copy(
                    scheduledTime = time,
                    scheduledDays = days,
                    isAlarmEnabled = alarm
                )
                else it
            }
        }
        closeTimePicker()
    }

    fun onShowInfoTooltip() {
        _uiState.update { it.copy(showInfoTooltip = true) }
    }

    fun onLikeClick(routineId: String) {
        _sourceRoutines.update { list ->
            list.map { r ->
                if (r.routineId == routineId)
                    r.copy(
                        isLiked = !r.isLiked,
                        likes = if (r.isLiked) r.likes - 1 else r.likes + 1
                    )
                else r
            }
        }
    }

    private fun uncheckAllRoutines() {
        _sourceRoutines.update { list -> list.map { it.copy(isChecked = false) } }
    }

    /** ✅ 요구사항 반영:
     *  - dayParam = 사용자가 탭에서 고른 요일(없으면 null)
     *  - 항상 sortType과 함께 서버로 전달 → 서버가 정렬 + (선택시)요일 필터를 함께 적용
     *  - 서버가 TIME+null을 아직 막아 에러면, 오늘 요일로 1회 폴백
     */
    private suspend fun fetchFromServer() {
        val state = _uiState.value
        val sortType = when (state.selectedSortOption) {
            SortOption.BY_TIME -> "TIME"
            SortOption.LATEST -> "LATEST"
            SortOption.POPULAR -> "POPULAR"
        }
        val dayParam: DayOfWeek? = state.selectedDay // ✅ sortType과 무관하게 그대로 전달

        try {
            val result = repo.getMyRoutines(
                sortType = sortType,
                dayOfWeek = dayParam,
                page = 0,
                size = 50
            )
            _sourceRoutines.value = result
        } catch (e: Exception) {
            val needFallback = (sortType == "TIME" && dayParam == null)
            if (needFallback) {
                val today = LocalDate.now(ZoneId.systemDefault()).dayOfWeek
                val fallback = repo.getMyRoutines(
                    sortType = "TIME",
                    dayOfWeek = today,
                    page = 0,
                    size = 50
                )
                _sourceRoutines.value = fallback
            } else {
                throw e
            }
        }
    }

    fun loadRoutines() {
        viewModelScope.launch { fetchFromServer() }
    }

    fun refreshRoutines() = loadRoutines()

    // -------- 프리뷰용 더미 --------
    @Suppress("unused")
    constructor() : this(
        repo = object : MyRoutineRepository {
            override suspend fun getMyRoutines(
                sortType: String,
                dayOfWeek: DayOfWeek?,
                page: Int,
                size: Int
            ): List<MyRoutineUi> {
                val mine = com.konkuk.moru.data.model.DummyData.feedRoutines
                    .filter { it.authorId == com.konkuk.moru.data.model.DummyData.MY_USER_ID }
                    .map {
                        MyRoutineUi(
                            routineId = it.routineId,
                            title = it.title,
                            imageUrl = it.imageUrl,
                            tags = it.tags,
                            likes = it.likes,
                            isLiked = it.isLiked,
                            isRunning = it.isRunning,
                            scheduledTime = it.scheduledTime,
                            scheduledDays = it.scheduledDays,
                            isAlarmEnabled = false,
                            isChecked = false,
                            authorId = it.authorId
                        )
                    }

                // ✅ 요일 교집합(선택 시에만)
                val filtered = if (dayOfWeek != null) {
                    mine.filter { it.scheduledDays.contains(dayOfWeek) }
                } else mine

                // ✅ 정렬 적용
                return when (sortType) {
                    "POPULAR" -> filtered.sortedByDescending { it.likes }
                    "LATEST" -> filtered.reversed() // 더미라 createdAt 없어서 역순으로 대체
                    else -> filtered.sortedBy { it.scheduledTime ?: java.time.LocalTime.MAX }
                }
            }

            override suspend fun getRoutineDetail(routineId: String) =
                throw UnsupportedOperationException("Preview only")

            override suspend fun deleteRoutine(routineId: String) = Unit
            override suspend fun getSchedules(routineId: String) =
                emptyList<com.konkuk.moru.domain.repository.MyRoutineSchedule>()

            override suspend fun deleteAllSchedules(routineId: String) = Unit
            override suspend fun deleteSchedule(routineId: String, scheduleId: String) = Unit
        }
    )
}