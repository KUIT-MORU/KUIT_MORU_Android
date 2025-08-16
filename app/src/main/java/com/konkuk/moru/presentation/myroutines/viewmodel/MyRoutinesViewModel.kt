package com.konkuk.moru.presentation.myroutines.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.dto.response.MyRoutine.MyRoutineUi
import com.konkuk.moru.data.mapper.toDayOfWeekOrNull
import com.konkuk.moru.data.mapper.toMyLocalTimeOrNull
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
import java.time.format.DateTimeFormatter
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

            val succeed = mutableListOf<String>()
            val failed = mutableListOf<String>()

            for (id in targets) {
                val ok = repo.deleteRoutineSafe(id)
                if (ok) succeed += id else failed += id
            }

            // 성공한 것만 즉시 제거(낙관적)
            if (succeed.isNotEmpty()) {
                _sourceRoutines.update { list -> list.filterNot { it.routineId in succeed } }
            }

            // UI 피드백
            _uiState.update {
                it.copy(
                    isDeleteMode = false,
                    showDeleteDialog = false,
                    showDeleteSuccessDialog = failed.isEmpty()
                )
            }

            // 실패 안내 토스트/스낵바가 필요하면 여기에 호출
            if (failed.isNotEmpty()) {
                // e.g. sendEvent(Snackbar("일부 루틴을 삭제하지 못했습니다. 잠시 후 다시 시도해 주세요."))
            }

            // 최신 서버 상태 재조회
            refreshRoutines()
        }
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
        val selectedDay = state.selectedDay

        val result: List<MyRoutineUi> = try {
            if (selectedDay == null) {
                // 날짜 미선택 → sortType만으로 전체
                repo.getMyRoutines(
                    sortType = sortType,
                    dayOfWeek = null,
                    page = 0, size = 50
                )
            } else {
                // 날짜 선택됨 → 먼저 요일 교집합 집합을 확보
                // 서버가 TIME에서만 요일 필터를 보장하므로 TIME으로 가져온 뒤, 원하는 정렬로 재정렬
                val dayFiltered = repo.getMyRoutines(
                    sortType = "TIME",
                    dayOfWeek = selectedDay,
                    page = 0, size = 50
                )
                when (sortType) {
                    "POPULAR" -> dayFiltered.sortedByDescending { it.likes }
                    "LATEST" -> dayFiltered.sortedByDescending { it.createdAt } // ISO-8601 기준
                    else -> dayFiltered.sortedBy { it.scheduledTime ?: LocalTime.MAX }
                }
            }
        } catch (e: Exception) {
            // 서버가 TIME+null을 아직 막아놨을 때 대비(이 경로는 selectedDay==null && sortType=="TIME"일 때만 의미 있음)
            if (sortType == "TIME" && selectedDay == null) {
                val today = LocalDate.now(ZoneId.systemDefault()).dayOfWeek
                repo.getMyRoutines("TIME", today, 0, 50)
            } else {
                throw e
            }
        }

        _sourceRoutines.value = result
    }

    fun loadRoutines() {
        viewModelScope.launch { fetchFromServer() }
    }

    fun refreshRoutines() = loadRoutines()


    private val HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss")


    //스케줄 관련 로직
    fun openTimePicker(routineId: String) {
        viewModelScope.launch {
            // 서버 스케줄 조회
            val schedules = repo.getSchedules(routineId)

            // 초기값 구성
            val initTime = schedules.firstOrNull()?.time?.toMyLocalTimeOrNull()
            val initDays = schedules.mapNotNull { it.dayOfWeek.toDayOfWeekOrNull() }.toSet()
            val initAlarm = schedules.any { it.alarmEnabled }
            val firstSchId = schedules.firstOrNull()?.id

            _uiState.update {
                it.copy(
                    editingRoutineId = routineId,
                    editingScheduleId = firstSchId,
                    initialTimeForSheet = initTime,
                    initialDaysForSheet = initDays,
                    initialAlarmForSheet = initAlarm
                )
            }
        }
    }

    fun closeTimePicker() {
        _uiState.update {
            it.copy(
                editingRoutineId = null,
                editingScheduleId = null,
                initialTimeForSheet = null,
                initialDaysForSheet = emptySet(),
                initialAlarmForSheet = true
            )
        }
    }

    fun onConfirmTimeSet(routineId: String, time: LocalTime, days: Set<DayOfWeek>, alarm: Boolean) {
        viewModelScope.launch {
            // 서버 PATCH (스케줄이 하나라도 있을 때)
            val schId = _uiState.value.editingScheduleId
            if (schId != null) {
                repo.updateSchedule(
                    routineId = routineId,
                    schId = schId,
                    time = time.format(HH_MM_SS),
                    days = days,
                    alarm = alarm
                )
            }
            // 로컬 UI 갱신
            _sourceRoutines.update { list ->
                list.map {
                    if (it.routineId == routineId) it.copy(
                        scheduledTime = time,
                        scheduledDays = days,
                        isAlarmEnabled = alarm
                    ) else it
                }
            }
            closeTimePicker()
        }
    }


}