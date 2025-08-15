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

@HiltViewModel // [추가]
class MyRoutinesViewModel @Inject constructor( // [추가] Hilt로 레포 주입
    private val repo: MyRoutineRepository
) : ViewModel() {

    // [유지] 서버에서 내려받은 "내 루틴(My*)" 전용 모델
    private val _sourceRoutines = MutableStateFlow<List<MyRoutineUi>>(emptyList())
    val routinesToDisplay: StateFlow<List<MyRoutineUi>> =
        _sourceRoutines.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(MyRoutinesUiState())
    val uiState: StateFlow<MyRoutinesUiState> = _uiState.asStateFlow()

    init {
        // 최초 로드
        refreshRoutines()

        // [추가] 정렬 옵션/요일이 바뀔 때마다 서버 재조회
        viewModelScope.launch {
            uiState
                .map { it.selectedSortOption to it.selectedDay }
                .distinctUntilChanged()
                .collect { refreshRoutines() }
        }
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

    fun onCheckRoutine(routineId: String, isChecked: Boolean) {
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
        viewModelScope.launch {
            // [변경] 서버 삭제 호출 후 재조회
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
        // [보류] 스케줄 생성/수정 API 스펙 나오면 여기서 repo 호출 추가
        _sourceRoutines.update { currentList ->
            currentList.map {
                if (it.routineId == routineId) it.copy(
                    scheduledTime = time,
                    scheduledDays = days,
                    isAlarmEnabled = alarm
                ) else it
            }
        }
        closeTimePicker()
    }

    fun onLikeClick(routineId: String) {
        // [임시] 좋아요 토글 API 스펙 없어서 낙관적 UI만
        _sourceRoutines.update { currentList ->
            currentList.map { routine ->
                if (routine.routineId == routineId) {
                    routine.copy(
                        isLiked = !routine.isLiked,
                        likes = if (routine.isLiked) routine.likes - 1 else routine.likes + 1
                    )
                } else routine
            }
        }
    }

    fun onShowInfoTooltip() { _uiState.update { it.copy(showInfoTooltip = true) } }

    private fun uncheckAllRoutines() {
        _sourceRoutines.update { currentList ->
            currentList.map { it.copy(isChecked = false) }
        }
    }

    // [변경] DummyData 제거 → 서버 호출
    // [변경] DummyData 제거 → 서버 호출
    private suspend fun fetchFromServer() {
        val state = _uiState.value
        val sortType = when (state.selectedSortOption) {
            SortOption.BY_TIME -> "TIME"
            SortOption.LATEST -> "LATEST"
            SortOption.POPULAR -> "POPULAR"
        }

        // [변경] TIME인데 선택 요일이 없으면 '오늘 요일'로 기본 세팅
        val dayForServer = if (sortType == "TIME") {
            state.selectedDay ?: LocalDate.now(ZoneId.systemDefault()).dayOfWeek
        } else null

        val result = repo.getMyRoutines(
            sortType = sortType,
            dayOfWeek = dayForServer,  // [변경]
            page = 0,
            size = 50
        )
        _sourceRoutines.value = result
    }

    fun loadRoutines() {
        viewModelScope.launch {
            fetchFromServer() // [변경] 더미 삭제 → 서버로 대체
        }
    }

    fun refreshRoutines() {
        loadRoutines()
    }

// MyRoutineScreenPreview
    @Suppress("unused") // 프리뷰에서만 사용
    constructor() : this(
        repo = object : MyRoutineRepository {
            override suspend fun getMyRoutines(
                sortType: String,
                dayOfWeek: java.time.DayOfWeek?,
                page: Int,
                size: Int
            ): List<MyRoutineUi> {
                // [추가] DummyData → MyRoutineUi 간단 매핑 (내 루틴만)
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

                val filtered = if (sortType == "TIME" && dayOfWeek != null) {
                    mine.filter { it.scheduledDays.contains(dayOfWeek) }
                } else mine

                return when (sortType) {
                    "POPULAR" -> filtered.sortedByDescending { it.likes }
                    "LATEST" -> filtered.reversed() // 더미이므로 뒤집어서 최신 흉내
                    else -> filtered.sortedBy { it.scheduledTime ?: java.time.LocalTime.MAX }
                }
            }

            override suspend fun getRoutineDetail(routineId: String) =
                throw UnsupportedOperationException("Preview only")

            override suspend fun deleteRoutine(routineId: String) = Unit
            override suspend fun getSchedules(routineId: String) = emptyList<com.konkuk.moru.domain.repository.MyRoutineSchedule>()
            override suspend fun deleteAllSchedules(routineId: String) = Unit
            override suspend fun deleteSchedule(routineId: String, scheduleId: String) = Unit
        }
    )
}


