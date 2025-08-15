package com.konkuk.moru.presentation.routinefocus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.konkuk.moru.presentation.home.RoutineStepData
import com.konkuk.moru.data.model.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalTime

class SharedRoutineViewModel : ViewModel() {
    
    init {
        Log.d("SharedRoutineViewModel", "🚀 SharedRoutineViewModel 생성됨!")
    }
    
    private val _selectedRoutineId = MutableStateFlow<Int?>(null)
    val selectedRoutineId: StateFlow<Int?> = _selectedRoutineId
    fun setSelectedRoutineId(id: Int) {
        Log.d("SharedRoutineViewModel", "🔄 setSelectedRoutineId: $id")
        _selectedRoutineId.value = id
    }

    // 원래 String 타입의 routineId 저장 (완료 처리용)
    private val _originalRoutineId = MutableStateFlow<String?>(null)
    val originalRoutineId: StateFlow<String?> = _originalRoutineId
    fun setOriginalRoutineId(id: String) {
        Log.d("SharedRoutineViewModel", "🔄 setOriginalRoutineId: $id")
        _originalRoutineId.value = id
    }

    // 루틴 제목
    private val _routineTitle = MutableStateFlow("")
    val routineTitle: StateFlow<String> = _routineTitle
    fun setRoutineTitle(title: String) {
        Log.d("SharedRoutineViewModel", "🔄 setRoutineTitle: $title")
        _routineTitle.value = title
    }

    // 루틴 카테고리 (집중 / 간편)
    private val _routineCategory = MutableStateFlow("")
    val routineCategory: StateFlow<String> = _routineCategory
    fun setRoutineCategory(category: String) {
        Log.d("SharedRoutineViewModel", "🔄 setRoutineCategory: $category")
        _routineCategory.value = category
    }

    // 총 소요시간
    private val _totalDuration = MutableStateFlow(0)
    val totalDuration: StateFlow<Int> = _totalDuration
    fun setTotalDuration(duration: Int) {
        Log.d("SharedRoutineViewModel", "🔄 setTotalDuration: ${duration}분")
        _totalDuration.value = duration
    }

    // 루틴 태그 리스트
    private val _routineTags = MutableStateFlow<List<String>>(emptyList())
    val routineTags: StateFlow<List<String>> = _routineTags
    fun setRoutineTags(tags: List<String>) {
        Log.d("SharedRoutineViewModel", "🔄 setRoutineTags: $tags")
        _routineTags.value = tags
    }

    // 간편 루틴 여부
    private val _isSimple = MutableStateFlow(false)
    val isSimple: StateFlow<Boolean> = _isSimple
    fun setIsSimple(isSimple: Boolean) {
        Log.d("SharedRoutineViewModel", "🔄 setIsSimple: $isSimple")
        _isSimple.value = isSimple
    }

    // 사용앱 리스트 (루틴 생성 시 선택한 앱들)
    private val _selectedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val selectedApps: StateFlow<List<AppInfo>> = _selectedApps
    fun setSelectedApps(apps: List<AppInfo>) {
        Log.d("SharedRoutineViewModel", "🔄 setSelectedApps: $apps")
        _selectedApps.value = apps
    }

    // 제목, 카테고리, 태그, 간편 루틴 여부 한꺼번에 설정
    fun setRoutineInfo(title: String, category: String, tags: List<String>, isSimple: Boolean = false) {
        Log.d("SharedRoutineViewModel", "🔄 setRoutineInfo 호출:")
        Log.d("SharedRoutineViewModel", "   - title: $title")
        Log.d("SharedRoutineViewModel", "   - category: $category")
        Log.d("SharedRoutineViewModel", "   - tags: $tags")
        Log.d("SharedRoutineViewModel", "   - isSimple: $isSimple")
        _routineTitle.value = title
        _routineCategory.value = category
        _routineTags.value = tags
        _isSimple.value = isSimple
        Log.d("SharedRoutineViewModel", "✅ setRoutineInfo 완료")
    }

    // 알림 시간 및 요일
    private val _scheduledTime = MutableStateFlow<LocalTime?>(null)
    val scheduledTime: StateFlow<LocalTime?> = _scheduledTime

    private val _scheduledDays = MutableStateFlow<Set<DayOfWeek>>(emptySet())
    val scheduledDays: StateFlow<Set<DayOfWeek>> = _scheduledDays

    fun setSchedule(time: LocalTime?, days: Set<DayOfWeek>) {
        _scheduledTime.value = time
        _scheduledDays.value = days
    }

    // 네비게이션 트리거 (카테고리 기반으로 변경)
    private val _startNavigation = MutableStateFlow<String?>(null)
    val startNavigation: StateFlow<String?> = _startNavigation

    fun onStartClick() {
        _startNavigation.value = _routineCategory.value
    }

    fun onNavigationHandled() {
        _startNavigation.value = null
    }

    // 선택된 스텝 리스트
    private val _selectedSteps = MutableStateFlow<List<RoutineStepData>>(emptyList())
    val selectedSteps: StateFlow<List<RoutineStepData>> = _selectedSteps

    fun setSelectedSteps(steps: List<RoutineStepData>) {
        _selectedSteps.value = steps
    }

    // 서버에서 받은 스텝 정보를 RoutineStepData로 변환하여 설정
    fun setStepsFromServer(steps: List<com.konkuk.moru.data.dto.response.RoutineStepResponse>, requiredTime: String = "") {
        Log.d("SharedRoutineViewModel", "🔄 setStepsFromServer 시작: ${steps.size}개 스텝, requiredTime=$requiredTime")
        
        val isSimple = requiredTime.isBlank() // requiredTime이 비어있으면 간편 루틴
        Log.d("SharedRoutineViewModel", "📱 루틴 타입: ${if (isSimple) "간편" else "집중"} (requiredTime=${if (requiredTime.isBlank()) "없음" else requiredTime})")
        
        val stepDataList = steps.map { step ->
            val durationInMinutes = if (step.duration != null) {
                // 스텝에 duration이 있으면 그대로 사용
                convertDurationToMinutes(step.duration)
            } else if (isSimple) {
                // 간편 루틴이면 소요시간 0
                0
            } else {
                // 집중 루틴이고 duration이 null이면 requiredTime을 기반으로 분배
                distributeRequiredTime(requiredTime, steps.size)
            }
            
            RoutineStepData(
                name = step.name,
                duration = durationInMinutes,
                isChecked = true
            )
        }
        
        Log.d("SharedRoutineViewModel", "✅ 변환 완료: ${stepDataList.size}개 RoutineStepData")
        stepDataList.forEachIndexed { index, stepData ->
            Log.d("SharedRoutineViewModel", "   - 최종 스텝 ${index + 1}: ${stepData.name} (${stepData.duration}분)")
        }
        
        _selectedSteps.value = stepDataList
        Log.d("SharedRoutineViewModel", "✅ _selectedSteps StateFlow 업데이트 완료")
    }

    // requiredTime을 스텝 개수에 맞게 분배
    private fun distributeRequiredTime(requiredTime: String, stepCount: Int): Int {
        if (requiredTime.isBlank() || stepCount == 0) {
            Log.w("SharedRoutineViewModel", "⚠️ requiredTime이 비어있거나 스텝이 없습니다. 기본값 1분을 사용합니다.")
            return 1
        }
        
        val totalMinutes = convertRequiredTimeToMinutes(requiredTime)
        val averageMinutes = totalMinutes / stepCount
        
        // 최소 1분은 보장
        return maxOf(averageMinutes, 1)
    }

    // ISO 8601 Duration 형식을 분 단위로 변환 (PT30M -> 30분)
    private fun convertRequiredTimeToMinutes(requiredTime: String): Int {
        return try {
            when {
                requiredTime.startsWith("PT") -> {
                    val timePart = requiredTime.substring(2) // "PT" 제거
                    when {
                        timePart.endsWith("H") -> {
                            // 시간 단위 (예: PT1H -> 60분)
                            val hours = timePart.removeSuffix("H").toIntOrNull() ?: 0
                            hours * 60
                        }
                        timePart.endsWith("M") -> {
                            // 분 단위 (예: PT30M -> 30분)
                            timePart.removeSuffix("M").toIntOrNull() ?: 0
                        }
                        timePart.endsWith("S") -> {
                            // 초 단위 (예: PT30S -> 1분)
                            val seconds = timePart.removeSuffix("S").toIntOrNull() ?: 0
                            (seconds + 59) / 60 // 올림 처리
                        }
                        else -> {
                            // 복합 형식 (예: PT1H30M -> 90분)
                            var totalMinutes = 0
                            var currentNumber = ""
                            
                            for (char in timePart) {
                                when (char) {
                                    'H' -> {
                                        totalMinutes += (currentNumber.toIntOrNull() ?: 0) * 60
                                        currentNumber = ""
                                    }
                                    'M' -> {
                                        totalMinutes += currentNumber.toIntOrNull() ?: 0
                                        currentNumber = ""
                                    }
                                    'S' -> {
                                        val seconds = currentNumber.toIntOrNull() ?: 0
                                        totalMinutes += (seconds + 59) / 60
                                        currentNumber = ""
                                    }
                                    else -> currentNumber += char
                                }
                            }
                            totalMinutes
                        }
                    }
                }
                else -> {
                    // 기존 "MM:SS" 형식 지원 (하위 호환성)
                    val parts = requiredTime.split(":")
                    val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
                    val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    minutes + (seconds / 60)
                }
            }
        } catch (e: Exception) {
            Log.w("SharedRoutineViewModel", "⚠️ requiredTime 변환 실패: $requiredTime, 기본값 1분 사용", e)
            1
        }
    }

    // ISO 8601 Duration 형식을 분 단위로 변환 (PT15M -> 15분)
    private fun convertDurationToMinutes(duration: String?): Int {
        // duration이 null이면 기본값 1분 반환
        if (duration == null) {
            Log.w("SharedRoutineViewModel", "⚠️ duration이 null입니다. 기본값 1분을 사용합니다.")
            return 1
        }
        
        return try {
            when {
                duration.startsWith("PT") -> {
                    val timePart = duration.substring(2) // "PT" 제거
                    when {
                        timePart.endsWith("H") -> {
                            // 시간 단위 (예: PT1H -> 60분)
                            val hours = timePart.removeSuffix("H").toIntOrNull() ?: 0
                            hours * 60
                        }
                        timePart.endsWith("M") -> {
                            // 분 단위 (예: PT15M -> 15분)
                            timePart.removeSuffix("M").toIntOrNull() ?: 0
                        }
                        timePart.endsWith("S") -> {
                            // 초 단위 (예: PT30S -> 1분)
                            val seconds = timePart.removeSuffix("S").toIntOrNull() ?: 0
                            (seconds + 59) / 60 // 올림 처리
                        }
                        else -> {
                            // 복합 형식 (예: PT1H30M -> 90분)
                            var totalMinutes = 0
                            var currentNumber = ""
                            
                            for (char in timePart) {
                                when (char) {
                                    'H' -> {
                                        totalMinutes += (currentNumber.toIntOrNull() ?: 0) * 60
                                        currentNumber = ""
                                    }
                                    'M' -> {
                                        totalMinutes += currentNumber.toIntOrNull() ?: 0
                                        currentNumber = ""
                                    }
                                    'S' -> {
                                        val seconds = currentNumber.toIntOrNull() ?: 0
                                        totalMinutes += (seconds + 59) / 60
                                        currentNumber = ""
                                    }
                                    else -> currentNumber += char
                                }
                            }
                            totalMinutes
                        }
                    }
                }
                else -> {
                    // 기존 "MM:SS" 형식 지원 (하위 호환성)
                    val parts = duration.split(":")
                    val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
                    val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    minutes + (seconds / 60)
                }
            }
        } catch (e: Exception) {
            // 변환 실패 시 기본값 반환
            Log.w("SharedRoutineViewModel", "⚠️ duration 변환 실패: $duration, 기본값 1분 사용", e)
            1
        }
    }


}
