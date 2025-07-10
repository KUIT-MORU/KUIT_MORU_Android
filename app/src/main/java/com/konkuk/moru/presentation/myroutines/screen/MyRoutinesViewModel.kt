package com.konkuk.moru.presentation.myroutines.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.moru.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

data class MyRoutine(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val likes: Int,
    var isLiked: Boolean,
    val isRunning: Boolean
)

class MyRoutinesViewModel @Inject constructor(
    // private val repository: RoutineRepository // TODO: 실제로는 Repository를 주입받아 사용해야 합니다.
) : ViewModel() {

    // 1. 화면에 표시될 루틴 목록을 관리하는 StateFlow
    // UI에서는 이 StateFlow를 구독(collect)하여 루틴 목록의 변경을 감지합니다.
    private val _routines = MutableStateFlow<List<MyRoutine>>(emptyList())
    val routines = _routines.asStateFlow()

    // 2. ViewModel이 생성될 때 초기 데이터를 로드합니다.
    init {
        loadRoutines()
    }

    // 3. 화면(Screen)에서 호출할 이벤트 핸들러 함수들
    fun onInfoClick() {
        // TODO: 정보 아이콘 클릭 관련 로직 구현 (예: 다이얼로그 표시)
        println("ViewModel: onInfoClick() 호출됨")
    }

    fun onTrashClick() {
        // TODO: 삭제 모드 진입 등 관련 로직 구현
        println("ViewModel: onTrashClick() 호출됨")
    }

    fun onTimeSetConfirm(routineId: Int, time: LocalTime, days: Set<DayOfWeek>, alarm: Boolean) {
        // TODO: Repository를 통해 루틴의 시간 정보를 저장하는 로직 구현
        println("ViewModel: Routine ID $routineId 의 시간 설정 완료 -> 시간: $time, 요일: $days, 알림: $alarm")
    }

    // 4. 루틴 데이터를 불러오는 함수 (현재는 샘플 데이터 사용)
    private fun loadRoutines() {
        viewModelScope.launch {
            // TODO: 나중에는 Repository에서 실제 데이터를 비동기적으로 불러와야 합니다.
            // val realRoutines = repository.getMyRoutines()
            // _routines.value = realRoutines

            // 지금은 샘플 데이터를 사용합니다.
            _routines.value = listOf(
                MyRoutine(1, "아침 운동", listOf("#모닝루틴", "#스트레칭"), 16, true, false),
                MyRoutine(2, "오전 명상", listOf("#마음챙김", "#집중"), 25, false, true),
                MyRoutine(3, "점심 후 산책", listOf("#건강", "#소화"), 8, false, false),
                MyRoutine(4, "영어 공부", listOf("#자기계발", "#외국어"), 42, true, true),
                MyRoutine(5, "일기 쓰기", listOf("#회고", "#감사"), 33, false, false)
            )
        }
    }
}