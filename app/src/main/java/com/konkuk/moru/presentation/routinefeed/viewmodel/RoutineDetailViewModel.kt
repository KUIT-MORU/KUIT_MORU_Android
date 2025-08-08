package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.SimilarRoutine
import com.konkuk.moru.data.model.findSimilarRoutinesByTags
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

// 상세 화면의 모든 UI 상태를 담는 데이터 클래스
data class RoutineDetailUiState(
    val routine: Routine? = null,
    val similarRoutines: List<SimilarRoutine> = emptyList(),
    val canBeAddedToMyRoutines: Boolean = false, // 이 루틴을 복사할 수 있는지 여부
    val isLoading: Boolean = true
)

class RoutineDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val targetRoutine = DummyData.feedRoutines.find { it.routineId == routineId }

            if (targetRoutine != null) {
                val similarRoutines = findSimilarRoutinesByTags(
                    targetRoutine = targetRoutine,
                    allRoutines = DummyData.feedRoutines
                )
                val isMyRoutine = targetRoutine.authorId == DummyData.MY_USER_ID
                val isAlreadyCopied = DummyData.feedRoutines.any {
                    it.authorId == DummyData.MY_USER_ID && it.title == targetRoutine.title
                }

                _uiState.update {
                    it.copy(
                        routine = targetRoutine,
                        similarRoutines = similarRoutines,
                        canBeAddedToMyRoutines = !isMyRoutine && !isAlreadyCopied,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, routine = null) }
            }
        }
    }

    // [핵심] '내 루틴으로 복사' 기능을 구현하는 함수
    fun copyRoutineToMyList() {
        _uiState.value.routine?.let { originalRoutine ->
            val myInfo = DummyData.dummyUsers.find { it.userId == DummyData.MY_USER_ID }

            // 1. 루틴 복사 및 authorId 변경
            val newRoutine = originalRoutine.copy(
                routineId = UUID.randomUUID().toString(), // 새 고유 ID 부여
                authorId = DummyData.MY_USER_ID,          // 작성자를 '나'로 변경
                authorName = myInfo?.nickname ?: "MORU (나)",
                authorProfileUrl = myInfo?.profileImageUrl,
                isLiked = false, // 상태 초기화
                likes = 0
            )

            // 2. 전체 데이터 목록에 새로 복사한 루틴 추가
            DummyData.feedRoutines.add(0, newRoutine)
            println("루틴(original id: ${originalRoutine.routineId})이 내 루틴으로 복사되었습니다. (new id: ${newRoutine.routineId})")

            // 3. 복사 후에는 버튼을 다시 보이지 않도록 UI 상태 갱신
            _uiState.update { it.copy(canBeAddedToMyRoutines = false) }
        }
    }
}