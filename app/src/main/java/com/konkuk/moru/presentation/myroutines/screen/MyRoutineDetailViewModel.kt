package com.konkuk.moru.presentation.myroutines.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
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
            _uiState.update { it.copy(routine = routine, isLoading = false) }
        }
    }

    /**
     * 특정 routineId를 가진 루틴을 DummyData에서 삭제합니다.
     */
    fun deleteRoutine(routineId: Int) {
        viewModelScope.launch {
            DummyData.feedRoutines.removeAll { it.routineId == routineId }
            _deleteCompleted.emit(true) // 삭제 완료 신호를 보냅니다.
        }
    }

    /**
     * 루틴의 설명과 카테고리를 업데이트합니다.
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
}