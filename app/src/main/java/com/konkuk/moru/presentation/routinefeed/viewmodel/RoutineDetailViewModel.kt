package com.konkuk.moru.presentation.routinefeed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.mapper.toRoutineModel
import com.konkuk.moru.data.mapper.toUiModel
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.data.model.SimilarRoutine
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// 상세 화면의 모든 UI 상태를 담는 데이터 클래스
data class RoutineDetailUiState(
    val routine: Routine? = null,
    val similarRoutines: List<SimilarRoutine> = emptyList(),
    val canBeAddedToMyRoutines: Boolean = false, // 이 루틴을 복사할 수 있는지 여부
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isLiking: Boolean = false,
    val isScrapping: Boolean = false,
)
@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val repository: RoutineFeedRepository // [추가]
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun setInitialRoutine(r: Routine) {
        _uiState.update { it.copy(routine = r, isLoading = true, errorMessage = null) }
    }



    fun toggleLikeSync() {
        val current = _uiState.value
        val r = current.routine ?: return
        if (current.isLiking || current.isLoading) return

        val wantLike = !r.isLiked

        // [추가] 1) 낙관적 업데이트
        val before = _uiState.value
        val bumpedLikes = (r.likes + if (wantLike) 1 else -1).coerceAtLeast(0) // [추가]
        _uiState.update { it.copy(routine = r.copy(isLiked = wantLike, likes = bumpedLikes), isLiking = true, errorMessage = null) } // [추가]

        viewModelScope.launch {
            runCatching {
                if (wantLike) repository.addLike(r.routineId) else repository.removeLike(r.routineId)
                repository.getRoutineDetail(r.routineId) // 서버 최신 재조회
            }.onSuccess { fresh ->
                _uiState.update {
                    it.copy(
                        routine = fresh.toRoutineModel(prev = it.routine), // [유지] 서버 값으로 동기화
                        isLiking = false
                    )
                }
            }.onFailure { e ->
                // [추가] 실패 시 롤백
                _uiState.value = before.copy(isLiking = false, errorMessage = e.message)
            }
        }
    }

    fun toggleScrapSync() {
        val current = _uiState.value
        val r = current.routine ?: return
        if (current.isScrapping || current.isLoading) return

        val wantScrap = !r.isBookmarked

        // [추가] 1) 낙관적 업데이트
        val before = _uiState.value
        _uiState.update { it.copy(routine = r.copy(isBookmarked = wantScrap), isScrapping = true, errorMessage = null) } // [추가]

        viewModelScope.launch {
            runCatching {
                if (wantScrap) repository.addScrap(r.routineId) else repository.removeScrap(r.routineId)
                repository.getRoutineDetail(r.routineId)
            }.onSuccess { fresh ->
                _uiState.update {
                    it.copy(
                        routine = fresh.toRoutineModel(prev = it.routine),
                        isScrapping = false
                    )
                }
            }.onFailure { e ->
                // [추가] 실패 시 롤백
                _uiState.value = before.copy(isScrapping = false, errorMessage = e.message)
            }
        }
    }


    fun loadRoutine(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.getRoutineDetail(routineId) // [수정] 서버 호출
            }.onSuccess { dto ->
                val merged = dto.toRoutineModel(prev = _uiState.value.routine)
                val similar = dto.similarRoutines?.map { it.toUiModel() } ?: emptyList()
                _uiState.update {
                    it.copy(
                        routine = merged,
                        similarRoutines = similar,
                        canBeAddedToMyRoutines = !dto.isOwner, // [수정] 서버 필드 사용
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
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