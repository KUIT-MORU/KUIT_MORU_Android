package com.konkuk.moru.presentation.routinefeed.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.data.model.DummyData
import com.konkuk.moru.domain.repository.InsightRepository
import com.konkuk.moru.domain.repository.RoutineFeedRepository
import com.konkuk.moru.presentation.routinefeed.data.LiveUserInfo // 👈 data 클래스를 참조하도록 수정
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
        loadRoutineSections()
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

    private fun loadRoutineSections() {
        // 기존에 Composable에 있던 로직을 그대로 가져옵니다.
        val sections = listOf(
            RoutineFeedSectionModel(
                title = "지금 가장 핫한 루틴은?",
                routines = DummyData.feedRoutines.filter { it.likes > 70 }.take(7)
            ),
            RoutineFeedSectionModel(
                "MORU님과 딱 맞는 루틴",
                routines = DummyData.feedRoutines.filter { it.authorName == "MORU" }.take(7)
            ),
            RoutineFeedSectionModel(
                "#지하철#독서",
                routines = DummyData.feedRoutines.filter {
                    it.tags.containsAll(listOf("지하철", "독서"))
                }.take(7)
            ),
            RoutineFeedSectionModel(
                "#운동#명상",
                routines = DummyData.feedRoutines.filter {
                    it.tags.containsAll(listOf("운동", "명상"))
                }.take(7)
            )
        )
        // UiState를 업데이트합니다.
        _uiState.update { it.copy(routineSections = sections) }
    }

    fun onNotificationViewed() {
        _uiState.update { it.copy(hasNotification = false) }
    }
}