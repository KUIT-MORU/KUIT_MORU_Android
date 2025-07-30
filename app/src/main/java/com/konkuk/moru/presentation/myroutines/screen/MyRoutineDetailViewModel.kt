package com.konkuk.moru.presentation.myroutines.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.AppInfo
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.RoutineStep
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MyRoutineDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyRoutineDetailUiState())
    val uiState = _uiState.asStateFlow()

    // 삭제 완료 후 이전 화면으로 돌아가기 위한 신호(Event)
    private val _deleteCompleted = MutableSharedFlow<Boolean>()
    val deleteCompleted = _deleteCompleted.asSharedFlow()

    private var originalRoutine: Routine? = null
    /**
     * 특정 routineId를 가진 '내 루틴'을 불러옵니다.
     */
    fun loadRoutine(routineId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // '내 루틴'만 찾도록 authorId 조건을 추가합니다.
            val routine = DummyData.feedRoutines.find {
                it.routineId == routineId && it.authorId == DummyData.MY_USER_ID
            }
            originalRoutine = routine?.copy()
            _uiState.update { it.copy(routine = routine, isLoading = false) }
        }
    }

    /**
     * 특정 routineId를 가진 루틴을 DummyData에서 삭제합니다.
     */
    fun restoreRoutine() {
        _uiState.update { it.copy(routine = originalRoutine) }
    }

    fun deleteRoutine(routineId: Int) {
        viewModelScope.launch {
            DummyData.feedRoutines.removeAll { it.routineId == routineId }
            _deleteCompleted.emit(true) // 삭제 완료 신호를 보냅니다.
        }
    }

    /**
     * 루틴의 설명과 카테고리를 업데이트합니다. -> 수정예정
     */
    fun updateRoutine(routineId: Int, newDescription: String, newCategory: String) {
        val index = DummyData.feedRoutines.indexOfFirst { it.routineId == routineId }
        if (index != -1) {
            val originalRoutine = DummyData.feedRoutines[index]
            // description과 category를 업데이트합니다.
            DummyData.feedRoutines[index] = originalRoutine.copy(
                description = newDescription,
                category = newCategory
            )
        }
    }

    fun saveChanges() {
        val updatedRoutine = uiState.value.routine ?: return
        val index = DummyData.feedRoutines.indexOfFirst { it.routineId == updatedRoutine.routineId }
        if (index != -1) {
            DummyData.feedRoutines[index] = updatedRoutine
        }
        originalRoutine = updatedRoutine.copy() // 저장 후 원본 데이터도 갱신
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { state ->
            state.copy(routine = state.routine?.copy(description = newDescription))
        }
    }

    fun updateCategory(newCategory: String) {
        _uiState.update { state ->
            state.copy(routine = state.routine?.copy(category = newCategory))
        }
    }

    fun deleteTag(tag: String) {
        _uiState.update { state ->
            val updatedTags = state.routine?.tags?.toMutableList()?.apply { remove(tag) }
            state.copy(routine = state.routine?.copy(tags = updatedTags ?: emptyList()))
        }
    }

    // TODO: 실제 앱에서는 다이얼로그 등을 통해 태그 이름을 입력받아야 합니다.
    fun addTag(tag: String = "새 태그") {
        if (tag.isBlank()) return
        _uiState.update { state ->
            val updatedTags = state.routine?.tags?.plus(tag)
            state.copy(routine = state.routine?.copy(tags = updatedTags ?: listOf(tag)))
        }
    }

    fun deleteStep(index: Int) {
        _uiState.update { state ->
            val currentSteps = state.routine?.steps?.toMutableList()
            currentSteps?.removeAt(index)
            state.copy(routine = state.routine?.copy(steps = currentSteps ?: emptyList()))
        }
    }

    // TODO: 실제 앱에서는 스텝 추가 화면으로 이동하거나 다이얼로그를 띄워야 합니다.
    fun addStep() {
        _uiState.update { state ->
            val newStep = RoutineStep(name = "활동명 입력", duration = "00:30")
            val updatedSteps = state.routine?.steps?.plus(newStep)
            state.copy(routine = state.routine?.copy(steps = updatedSteps ?: listOf(newStep)))
        }
    }


    fun updateStepName(index: Int, newName: String) {
        _uiState.update { state ->
            state.routine?.let { routine ->
                val currentSteps = routine.steps.toMutableList()
                // 인덱스가 유효한 범위 내에 있는지 확인
                if (index in currentSteps.indices) {
                    // 해당 인덱스의 스텝을 새로운 이름으로 교체
                    currentSteps[index] = currentSteps[index].copy(name = newName)
                }
                // 업데이트된 스텝 리스트로 routine 상태를 갱신
                state.copy(routine = routine.copy(steps = currentSteps))
            } ?: state // routine이 null이면 기존 상태 반환
        }
    }


    fun moveStep(from: Int, to: Int) {
        _uiState.update { state ->
            val currentSteps = state.routine?.steps?.toMutableList() ?: return@update state
            val movedItem = currentSteps.removeAt(from)
            currentSteps.add(to, movedItem)
            state.copy(routine = state.routine?.copy(steps = currentSteps))
        }
    }

    /**
     * [추가] 사용 앱을 삭제합니다.
     */
    fun deleteApp(appToDelete: AppInfo) {
        _uiState.update { state ->
            val updatedApps = state.routine?.usedApps?.filter { it.name != appToDelete.name }
            state.copy(routine = state.routine?.copy(usedApps = updatedApps ?: emptyList()))
        }
    }


    fun addApp() {
        _uiState.update { state ->
            // 예시로 새 앱 추가
            val newApp = AppInfo(name = "새로운 앱", iconUrl = "https://uxwing.com/wp-content/themes/uxwing/download/hand-gestures/good-icon.png")
            val updatedApps = state.routine?.usedApps?.plus(newApp)
            state.copy(routine = state.routine?.copy(usedApps = updatedApps ?: listOf(newApp)))
        }
    }





}