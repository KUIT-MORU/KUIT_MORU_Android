package com.konkuk.moru.presentation.routinefeed.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.mapper.toRoutineModel
import com.konkuk.moru.domain.repository.AuthRepository
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo
import com.konkuk.moru.presentation.routinefeed.screen.main.RoutineFeedSectionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutineFeedUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val hasNotification: Boolean = true,
    val liveUsers: List<LiveUserInfo> = emptyList(),
    val routineSections: List<RoutineFeedSectionModel> = emptyList()
)

@HiltViewModel
class RoutineFeedViewModel @Inject constructor(
    private val routineFeedRepository: RoutineFeedRepository // 👈 주입받는 타입 변경
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutineFeedUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLiveUsers()
        loadRoutineFeed()
    }

    private fun loadLiveUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 👇 새 Repository의 함수들을 호출
                val liveUsersResult = routineFeedRepository.getLiveUsers()
                //val routineSectionsResult = routineFeedRepository.getRoutineSections()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        liveUsers = liveUsersResult,
                        //routineSections = routineSectionsResult
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "데이터를 불러오는 데 실패했습니다.\n${e.message}"
                    )
                }
            }
        }
    }

    private fun loadRoutineFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Repository를 통해 서버로부터 DTO 받아오기
                val response = routineFeedRepository.getRoutineFeed()

                // 2. 받아온 DTO를 UI에서 사용할 `RoutineFeedSectionModel` 리스트로 변환
                val sections = buildList {
                    if (response.hotRoutines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "지금 가장 핫한 루틴은?",
                                routines = response.hotRoutines.map { it.toRoutineModel() } // ✅ 매퍼 함수 사용
                            )
                        )
                    }
                    if (response.personalRoutines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "MORU님과 딱 맞는 루틴",
                                routines = response.personalRoutines.map { it.toRoutineModel() }
                            )
                        )
                    }
                    if (response.tagPairSection1.routines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "#${response.tagPairSection1.tag1} #${response.tagPairSection1.tag2}",
                                routines = response.tagPairSection1.routines.map { it.toRoutineModel() }
                            )
                        )
                    }
                    if (response.tagPairSection2.routines.isNotEmpty()) {
                        add(
                            RoutineFeedSectionModel(
                                title = "#${response.tagPairSection2.tag1} #${response.tagPairSection2.tag2}",
                                routines = response.tagPairSection2.routines.map { it.toRoutineModel() }
                            )
                        )
                    }
                }

                // 3. 변환된 데이터로 UI State 업데이트
                _uiState.update {
                    it.copy(isLoading = false, routineSections = sections)
                }
            } catch (e: Exception) {
                Log.e("RoutineFeedViewModel", "Failed to load routine feed", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "루틴 피드를 불러오는 데 실패했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    fun onNotificationViewed() {
        _uiState.update { it.copy(hasNotification = false) }
    }

    fun toggleLike(routineId: String) {
        val sections = _uiState.value.routineSections

        // 현재 화면에 있는 모든 루틴 목록에서 ID가 일치하는 루틴을 찾습니다.
        val updatedSections = sections.map { section ->
            section.copy(
                routines = section.routines.map { routine ->
                    if (routine.routineId == routineId) {
                        // ID가 일치하면, isLiked 상태를 뒤집고 likes 수를 조절합니다.
                        val newLikedState = !routine.isLiked
                        val newLikeCount =
                            if (newLikedState) routine.likes + 1 else routine.likes - 1

                        routine.copy(isLiked = newLikedState, likes = newLikeCount)
                    } else {
                        routine
                    }
                }
            )
        }

        // 새로운 루틴 목록으로 UI State를 업데이트합니다.
        // Compose는 State가 변경된 것을 감지하고 화면을 자동으로 다시 그립니다.
        _uiState.update { it.copy(routineSections = updatedSections) }
    }
}


