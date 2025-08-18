package com.konkuk.moru.presentation.myactivity.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

data class CompletedRoutine(
    val title: String,
    val tags: List<String>,
    val isComplete: Boolean = true,
    val time: String = "00:00:00",
    val completedAt: Long = System.currentTimeMillis()
)

class ActRecordViewModel : ViewModel() {
    private val _completedRoutines = MutableStateFlow<List<CompletedRoutine>>(emptyList())
    val completedRoutines: StateFlow<List<CompletedRoutine>> = _completedRoutines.asStateFlow()

    init {
        // 기존 더미 데이터로 초기화
        val dummyData = listOf(
            CompletedRoutine("루틴 이름 1", listOf("공부", "운동"), false),
            CompletedRoutine("루틴 이름 2", listOf("자기계발", "아침루틴"), true),
            CompletedRoutine("루틴 이름 3", listOf("영어", "책읽기"), true),
            CompletedRoutine("루틴 이름 4", listOf("일기쓰기", "스트레칭"), false),
            CompletedRoutine("루틴 이름 5", listOf("명상", "감사일기"), true),
            CompletedRoutine("루틴 이름 6", listOf("저녁루틴", "복습"), false),
            CompletedRoutine("루틴 이름 7", listOf("알고리즘", "CS공부"), true),
            CompletedRoutine("루틴 이름 8", listOf("걷기", "산책"), false),
            CompletedRoutine("루틴 이름 9", listOf("플러터", "Compose"), true),
            CompletedRoutine("루틴 이름 10", listOf("뉴스보기", "시사공부"), false),
            CompletedRoutine("루틴 이름 6", listOf("저녁루틴", "복습"), false),
            CompletedRoutine("루틴 이름 7", listOf("알고리즘", "CS공부"), true),
            CompletedRoutine("루틴 이름 8", listOf("걷기", "산책"), false),
            CompletedRoutine("루틴 이름 9", listOf("플러터", "Compose"), true),
            CompletedRoutine("루틴 이름 10", listOf("뉴스보기", "시사공부"), false)
        )
        _completedRoutines.value = dummyData
    }

    fun addCompletedRoutine(title: String, tags: List<String>, time: String = "00:00:00") {
        Log.d("ActRecordViewModel", "🔄 addCompletedRoutine 호출됨")
        Log.d("ActRecordViewModel", "   - 제목: $title")
        Log.d("ActRecordViewModel", "   - 태그: $tags")
        Log.d("ActRecordViewModel", "   - 시간: $time")
        
        val newRoutine = CompletedRoutine(
            title = title,
            tags = tags,
            isComplete = true,
            time = time
        )
        
        val currentList = _completedRoutines.value.toMutableList()
        Log.d("ActRecordViewModel", "📊 현재 루틴 개수: ${currentList.size}")
        
        currentList.add(0, newRoutine) // 맨 앞에 추가
        _completedRoutines.value = currentList
        
        Log.d("ActRecordViewModel", "✅ 루틴 추가 완료. 새로운 개수: ${_completedRoutines.value.size}")
    }
}
