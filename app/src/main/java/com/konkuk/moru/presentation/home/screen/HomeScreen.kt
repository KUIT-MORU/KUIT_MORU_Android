package com.konkuk.moru.presentation.home.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.R
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.home.FabConstants
import com.konkuk.moru.presentation.home.RoutineStepData
import com.konkuk.moru.presentation.home.component.HomeFloatingActionButton
import com.konkuk.moru.presentation.home.component.HomeTopAppBar
import com.konkuk.moru.presentation.home.component.RoutineCardList
import com.konkuk.moru.presentation.home.component.TodayRoutinePager
import com.konkuk.moru.presentation.home.component.TodayWeekTab
import com.konkuk.moru.presentation.home.component.WeeklyCalendarView
import com.konkuk.moru.presentation.home.viewmodel.HomeRoutinesViewModel
import com.konkuk.moru.presentation.home.viewmodel.UserViewModel
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.presentation.routinefocus.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import com.konkuk.moru.core.datastore.SchedulePreference
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.collections.first
import kotlin.collections.isNotEmpty
import kotlin.collections.mapNotNull

fun convertDurationToMinutes(duration: String): Int {
    val parts = duration.split(":")
    val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return minutes + (seconds / 60)
}

// 라벨 포맷(루틴 제목을 최대 10글자로 제한하고 4글자씩 줄바꿈, 최대 3줄)
private fun Routine.toCalendarLabel(): String {
    val title = this.title.take(10) // 최대 10글자로 제한
    
    return when {
        // 4글자 이하면 그대로 사용
        title.length <= 4 -> title
        // 5-8글자면 4글자씩 2줄로 줄바꿈
        title.length <= 8 -> {
            val firstLine = title.take(4)
            val secondLine = title.drop(4)
            "$firstLine\n$secondLine"
        }
        // 9-10글자면 4글자씩 3줄로 줄바꿈
        else -> {
            val firstLine = title.take(4)
            val secondLine = title.take(8).drop(4)
            val thirdLine = title.drop(8)
            "$firstLine\n$secondLine\n$thirdLine"
        }
    }
}

// 이번주(월~일) 맵 생성: dayOfMonth -> [라벨, 라벨, ...]
private fun buildWeeklyMap(routines: List<Routine>): Pair<Map<Int, List<String>>, Int> {
    val today = LocalDate.now()
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Log.d("HomeScreen", "buildWeeklyMap 시작: routines.size=${routines.size}")
    routines.forEach { routine ->
        Log.d("HomeScreen", "루틴: ${routine.title}, scheduledDays=${routine.scheduledDays}, scheduledTime=${routine.scheduledTime}")
    }

    val map = weekDates.associate { date ->
        Log.d("HomeScreen", "🔍 날짜 ${date.dayOfMonth}(${date.dayOfWeek}) 처리 시작")
        
        val labels = routines
            .filter { r ->
                // 🔸 요일 세팅된 루틴만 주간에 배치
                val hasScheduledDays = r.scheduledDays.isNotEmpty()
                val containsDayOfWeek = r.scheduledDays.contains(date.dayOfWeek)

                // 더 많은 루틴을 표시하기 위한 개선된 로직
                val shouldShow = when {
                    // 1. 서버 스케줄에 scheduledDays가 설정되어 있고 해당 요일에 포함되는 경우 (우선순위 1)
                    hasScheduledDays && containsDayOfWeek -> {
                        Log.d("HomeScreen", "✅ ${r.title}: 서버 스케줄에 ${date.dayOfWeek} 포함됨")
                        true
                    }
                    // 2. scheduledDays가 비어있지만 오늘 요일인 경우 (우선순위 2)
                    !hasScheduledDays && date.dayOfWeek == today.dayOfWeek -> {
                        Log.d("HomeScreen", "✅ ${r.title}: 오늘 루틴으로 ${date.dayOfWeek}에 배치")
                        true
                    }
                    // 3. 그 외의 경우는 표시하지 않음 (임시 분산 배치 제거)
                    else -> {
                        Log.d("HomeScreen", "❌ ${r.title}: 조건에 맞지 않음 (hasScheduledDays=$hasScheduledDays, containsDayOfWeek=$containsDayOfWeek)")
                        false
                    }
                }

                Log.d("HomeScreen", "📊 날짜 ${date.dayOfMonth}(${date.dayOfWeek}): ${r.title} - scheduledDays=${r.scheduledDays}, hasScheduledDays=$hasScheduledDays, containsDayOfWeek=$containsDayOfWeek, shouldShow=$shouldShow")
                shouldShow
            }
            .sortedBy { it.scheduledTime ?: LocalTime.MAX }
            .map { it.toCalendarLabel() }

        Log.d("HomeScreen", "📅 날짜 ${date.dayOfMonth}에 최종 표시될 라벨: $labels (${labels.size}개)")
        date.dayOfMonth to labels
    }

    Log.d("HomeScreen", "최종 주간 맵: $map")
    return map to today.dayOfMonth
}

// requiredTime을 기반으로 간편/집중 루틴 구분
private fun determineRoutineType(requiredTime: String): Boolean {
    // requiredTime이 비어있으면 간편 루틴, 있으면 집중 루틴
    return requiredTime.isBlank()
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
        Log.w("HomeScreen", "⚠️ requiredTime 변환 실패: $requiredTime", e)
        0
    }
}

// 홈 메인 페이지
@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedRoutineViewModel,
    modifier: Modifier = Modifier,
    fabOffsetY: MutableState<Float>,
    todayTabOffsetY: MutableState<Float>,
    onShowOnboarding: () -> Unit = {},
) {
    Log.d("HomeScreen", "🚀 HomeScreen Composable 시작!")
    Log.d("HomeScreen", "📱 앱이 실행되고 있습니다!")
    Log.d("HomeScreen", "🔍 navController: $navController")
    Log.d("HomeScreen", "🔍 sharedViewModel: $sharedViewModel")

    val userVm: UserViewModel = hiltViewModel()
    val nickname by userVm.nickname.collectAsState()
    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "🔄 userVm.loadMe() 호출")
        userVm.loadMe()
    }

    // Context 가져오기
    val context = LocalContext.current

    // 오늘 탭 표시용(서버 응답 + 순서 복원/완료 시 뒤로)
    val todayRoutines = remember { mutableStateListOf<Routine>() }

    // 포커스 화면에서 완료한 루틴을 홈으로 돌려받아 카드 뒤로 보내기
    val homeEntry = remember(navController) {
        navController.getBackStackEntry(Route.Home.route)
    }

    // 진행중 루틴 ID 스택 수신 (Int 안정 ID 리스트)
    val runningIds by homeEntry.savedStateHandle
        .getStateFlow<List<Int>>("runningRoutineIds", emptyList())
        .collectAsState(initial = emptyList())

    // 서버 오늘 루틴
    val homeVm: HomeRoutinesViewModel = hiltViewModel()
    
    Log.d("HomeScreen", "🔍 homeVm 인스턴스: $homeVm")
    Log.d("HomeScreen", "🔍 homeVm 클래스: ${homeVm.javaClass.simpleName}")

    // SharedRoutineViewModel을 HomeRoutinesViewModel에 설정
    LaunchedEffect(Unit) {
        homeVm.setSharedRoutineViewModel(sharedViewModel)
        Log.d("HomeScreen", "✅ SharedRoutineViewModel을 HomeRoutinesViewModel에 설정 완료")
    }

    // ① Today(오늘용)
    val serverRoutines by homeVm.serverRoutines.collectAsState()
    // ② 내 루틴 전체(하단 카드용)
    val myRoutines by homeVm.myRoutines.collectAsState()
    // ③ 스케줄 정보가 병합된 루틴 (주간 달력용)
    val scheduledRoutines by homeVm.scheduledRoutines.collectAsState()

    // 하이라이트 대상 보관 (진행중인 모든 루틴)
    var highlightIds by remember { mutableStateOf<List<Int>>(emptyList()) }

    // runningIds 스택 변경 시 간편 루틴만 하이라이트 ID로 설정
    LaunchedEffect(runningIds, myRoutines) {
        if (runningIds.isNotEmpty()) {
            // 스택에서 간편 루틴만 필터링하여 하이라이트 대상으로 설정
            val simpleRoutineIds = mutableListOf<Int>()
            
            runningIds.forEach { id ->
                val routine = myRoutines.find { it.routineId.toStableIntId() == id }
                val isSimpleRoutine = routine?.let { determineRoutineType(it.requiredTime) } ?: false
                
                if (isSimpleRoutine) {
                    simpleRoutineIds.add(id)
                    Log.d("HomeScreen", "🎯 간편 루틴 하이라이트 추가: ${routine?.title} (ID: $id)")
                } else {
                    Log.d("HomeScreen", "🎯 집중 루틴 하이라이트 제외: ${routine?.title} (ID: $id)")
                }
            }
            
            highlightIds = simpleRoutineIds
            Log.d("HomeScreen", "🎯 간편 루틴만 하이라이트 ID 설정: $highlightIds (전체 스택 크기: ${runningIds.size})")
        } else {
            // 스택이 비어있으면 하이라이트 해제
            if (highlightIds.isNotEmpty()) {
                highlightIds = emptyList()
                Log.d("HomeScreen", "🎯 스택이 비어있으므로 하이라이트 해제")
            }
        }
    }

    // 하이라이트 ID 변경 시 로그 추가
    LaunchedEffect(highlightIds) {
        Log.d("HomeScreen", "🎯 하이라이트 IDs 변경됨: $highlightIds")
    }
    


    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "🔄 LaunchedEffect(Unit) 실행 시작")
        Log.d("HomeScreen", "🔍 homeVm 상태 확인: $homeVm")
        Log.d("HomeScreen", "loadTodayRoutines() 호출")
        try {
            homeVm.loadTodayRoutines()
            Log.d("HomeScreen", "✅ loadTodayRoutines() 호출 완료")
        } catch (e: Exception) {
            Log.e("HomeScreen", "❌ loadTodayRoutines() 호출 실패", e)
        }
        
        // 하단 카드용 전체 목록도 로드
        try {
            homeVm.loadMyRoutines()
            Log.d("HomeScreen", "✅ loadMyRoutines() 호출 완료")
        } catch (e: Exception) {
            Log.e("HomeScreen", "❌ loadMyRoutines() 호출 실패", e)
        }
    }

    // 화면이 다시 활성화될 때 데이터 리로드
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            if (destination.route == Route.Home.route) {
                Log.d("HomeScreen", "🔄 Home 화면 활성화 감지 - 데이터 리로드")
                try {
                    homeVm.loadMyRoutines()
                    Log.d("HomeScreen", "✅ 화면 활성화 시 loadMyRoutines() 호출 완료")
                    
                    // 오늘 루틴도 다시 로드하여 스케줄 정보 업데이트
                    homeVm.loadTodayRoutines()
                    Log.d("HomeScreen", "✅ 화면 활성화 시 loadTodayRoutines() 호출 완료")
                } catch (e: Exception) {
                    Log.e("HomeScreen", "❌ 화면 활성화 시 데이터 리로드 실패", e)
                }
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    // 서버 데이터 로드 후 스케줄 정보와 병합
    LaunchedEffect(serverRoutines) {
        if (serverRoutines.isNotEmpty()) {
            Log.d("HomeScreen", "서버 데이터 로드 완료, 스케줄 정보와 병합 시작")
            
            // 각 루틴의 스케줄 정보를 서버에서 가져와서 병합 (비동기 처리)
            val routinesWithSchedules = serverRoutines.map { routine ->
                routine
            }
            
            // 병합된 루틴을 todayRoutines에 설정
            todayRoutines.clear()
            todayRoutines.addAll(routinesWithSchedules)
            
            // 로컬 스케줄 정보도 병합 (기존 기능 유지)
            homeVm.mergeWithLocalSchedule(context)
            
            // 스케줄 정보를 비동기로 가져와서 업데이트 (코루틴으로 병렬 처리)
            Log.d("HomeScreen", "🔄 스케줄 정보 가져오기 시작: ${serverRoutines.size}개 루틴")
            
            CoroutineScope(Dispatchers.IO).launch {
                val scheduleJobs = serverRoutines.map { routine ->
                    async {
                        Log.d("HomeScreen", "🔍 루틴 스케줄 조회: ${routine.title} (ID: ${routine.routineId})")
                        try {
                            val schedules = homeVm.getRoutineSchedules(routine.routineId)
                            Log.d("HomeScreen", "📊 스케줄 응답: ${routine.title} - ${schedules.size}개 스케줄")
                            
                            schedules.forEachIndexed { index, schedule ->
                                Log.d("HomeScreen", "   스케줄[$index]: dayOfWeek=${schedule.dayOfWeek}, time=${schedule.time}, alarmEnabled=${schedule.alarmEnabled}")
                            }
                            
                            if (schedules.isNotEmpty()) {
                                // 스케줄 정보를 DayOfWeek와 LocalTime으로 변환
                                val scheduledDays: Set<DayOfWeek> = schedules.mapNotNull { schedule ->
                                    val dayOfWeek = when (schedule.dayOfWeek.uppercase()) {
                                        "MON" -> DayOfWeek.MONDAY
                                        "TUE" -> DayOfWeek.TUESDAY
                                        "WED" -> DayOfWeek.WEDNESDAY
                                        "THU" -> DayOfWeek.THURSDAY
                                        "FRI" -> DayOfWeek.FRIDAY
                                        "SAT" -> DayOfWeek.SATURDAY
                                        "SUN" -> DayOfWeek.SUNDAY
                                        else -> {
                                            Log.w("HomeScreen", "⚠️ 알 수 없는 요일 형식: ${schedule.dayOfWeek}")
                                            null
                                        }
                                    }
                                    Log.d("HomeScreen", "   변환: ${schedule.dayOfWeek} -> $dayOfWeek")
                                    dayOfWeek
                                }.toSet()
                                
                                val scheduledTime = if (schedules.isNotEmpty()) {
                                    try {
                                        val time = LocalTime.parse(schedules.first().time, DateTimeFormatter.ofPattern("HH:mm:ss"))
                                        Log.d("HomeScreen", "   시간 변환: ${schedules.first().time} -> $time")
                                        time
                                    } catch (e: Exception) {
                                        Log.e("HomeScreen", "❌ 시간 파싱 실패: ${schedules.first().time}", e)
                                        null
                                    }
                                } else null
                                
                                Log.d("HomeScreen", "✅ 스케줄 정보 병합: ${routine.title} - 요일: $scheduledDays, 시간: $scheduledTime")
                                
                                Triple(routine.routineId, scheduledDays, scheduledTime)
                            } else {
                                Log.d("HomeScreen", "⚠️ 스케줄 정보 없음: ${routine.title}")
                                Triple(routine.routineId, emptySet<DayOfWeek>(), null)
                            }
                        } catch (e: Exception) {
                            Log.e("HomeScreen", "❌ 스케줄 정보 가져오기 실패: ${routine.title}", e)
                            Triple(routine.routineId, emptySet<DayOfWeek>(), null)
                        }
                    }
                }
                
                // 모든 스케줄 정보를 병렬로 가져온 후 UI 업데이트
                val scheduleResults = scheduleJobs.awaitAll()
                
                scheduleResults.forEach { (routineId, scheduledDays, scheduledTime) ->
                    val index = todayRoutines.indexOfFirst { it.routineId == routineId }
                    if (index >= 0) {
                        val updatedRoutine = todayRoutines[index].copy(scheduledDays = scheduledDays, scheduledTime = scheduledTime)
                        todayRoutines[index] = updatedRoutine
                        Log.d("HomeScreen", "✅ 루틴 업데이트 완료: ${updatedRoutine.title}")
                    } else {
                        Log.w("HomeScreen", "⚠️ todayRoutines에서 루틴을 찾을 수 없음: routineId=$routineId")
                    }
                }
                
                Log.d("HomeScreen", "🎉 모든 스케줄 정보 로딩 완료!")
            }
            
                         // 서버 데이터 로드 후 runningIds 스택이 있으면 myRoutines에서 해당 루틴들을 isRunning=true로 설정하고 맨 앞으로 이동 (TODAY 탭은 제외)
             if (runningIds.isNotEmpty()) {
                 Log.d("HomeScreen", "🔄 서버 데이터 로드 후 runningIds 스택 처리: $runningIds")
                 
                 // myRoutines에서 진행중인 루틴들을 맨 앞으로 이동 (TODAY 탭은 하이라이트/이동 없음)
                 val myRoutinesList = myRoutines.toList()
                 val updatedRoutines = myRoutinesList.toMutableList()
                 
                 // 스택의 순서대로 (최신이 맨 위) 진행중인 루틴들을 맨 앞으로 이동 (간편 루틴만 isRunning=true)
                 val runningRoutines = mutableListOf<Routine>()
                 
                 runningIds.reversed().forEach { id ->
                     val myIdx = updatedRoutines.indexOfFirst { it.routineId.toStableIntId() == id }
                     if (myIdx >= 0) {
                         val routine = updatedRoutines[myIdx]
                         val isSimpleRoutine = determineRoutineType(routine.requiredTime)
                         
                         Log.d("HomeScreen", "✅ myRoutines에서 진행중 루틴 발견: ${routine.title} (간편: $isSimpleRoutine)")
                         
                         val runningRoutine = updatedRoutines.removeAt(myIdx)
                         val updatedRunningRoutine = if (isSimpleRoutine) {
                             runningRoutine.copy(isRunning = true) // 간편 루틴만 하이라이트
                         } else {
                             runningRoutine.copy(isRunning = false) // 집중 루틴은 하이라이트 안함
                         }
                         runningRoutines.add(updatedRunningRoutine)
                         
                         Log.d("HomeScreen", "🔄 진행중 루틴 수집: ${updatedRunningRoutine.title} (isRunning: ${updatedRunningRoutine.isRunning})")
                     } else {
                         Log.w("HomeScreen", "⚠️ myRoutines에서 runningId=$id 를 찾을 수 없음")
                     }
                 }
                 
                 // 스택 순서대로 맨 앞에 추가 (최신이 맨 위)
                 runningRoutines.reversed().forEach { routine ->
                     updatedRoutines.add(0, routine)
                     Log.d("HomeScreen", "🔄 스택 순서대로 맨 앞에 추가: ${routine.title}")
                 }
                 
                 homeVm.updateMyRoutines(updatedRoutines)
                 Log.d("HomeScreen", "✅ myRoutines 스택 기반 업데이트 완료")
             }
        }
    }

    // 네비게이션 트리거 처리
    val navigateToRoutineFocus by homeEntry.savedStateHandle
        .getStateFlow<String?>("navigateToRoutineFocus", null)
        .collectAsState(initial = null)
    
    val navigateToRoutineSimpleRun by homeEntry.savedStateHandle
        .getStateFlow<String?>("navigateToRoutineSimpleRun", null)
        .collectAsState(initial = null)

    LaunchedEffect(navigateToRoutineFocus) {
        Log.d("HomeScreen", "🔄 LaunchedEffect(navigateToRoutineFocus) 실행: $navigateToRoutineFocus")
        navigateToRoutineFocus?.let { routineId ->
            Log.d("HomeScreen", "✅ 네비게이션 트리거 감지: routineId=$routineId")
            // 스텝 정보 로드 완료 후 네비게이션
            kotlinx.coroutines.delay(500)
            Log.d("HomeScreen", "🔄 500ms 딜레이 완료, 네비게이션 시작")
            navController.navigate(Route.RoutineFocusIntro.route)
            Log.d("HomeScreen", "✅ RoutineFocusIntro로 네비게이션 완료")
            // 트리거 초기화
            homeEntry.savedStateHandle["navigateToRoutineFocus"] = null
            Log.d("HomeScreen", "🔄 네비게이션 트리거 초기화 완료")
        }
    }
    
    LaunchedEffect(navigateToRoutineSimpleRun) {
        Log.d("HomeScreen", "🔄 LaunchedEffect(navigateToRoutineSimpleRun) 실행: $navigateToRoutineSimpleRun")
        navigateToRoutineSimpleRun?.let { routineId ->
            Log.d("HomeScreen", "✅ 간편 루틴 네비게이션 트리거 감지: routineId=$routineId")
            // 스텝 정보 로드 완료 후 네비게이션
            kotlinx.coroutines.delay(500)
            Log.d("HomeScreen", "🔄 500ms 딜레이 완료, 간편 루틴 네비게이션 시작")
            navController.navigate(Route.RoutineSimpleRun.route)
            Log.d("HomeScreen", "✅ RoutineSimpleRun으로 네비게이션 완료")
            // 트리거 초기화
            homeEntry.savedStateHandle["navigateToRoutineSimpleRun"] = null
            Log.d("HomeScreen", "🔄 간편 루틴 네비게이션 트리거 초기화 완료")
        }
    }

    // routineDetail이 로드되면 스텝 정보를 SharedRoutineViewModel에 설정
    LaunchedEffect(homeVm.routineDetail.value) {
        val detail = homeVm.routineDetail.value
        if (detail != null) {
            Log.d("HomeScreen", "✅ LaunchedEffect(routineDetail): 스텝 정보 설정")
            // requiredTime을 함께 전달
            val currentRoutine = todayRoutines.find { it.routineId == detail.id }
            val requiredTime = currentRoutine?.requiredTime ?: ""
            Log.d("HomeScreen", "📱 requiredTime 전달: $requiredTime")
            sharedViewModel.setStepsFromServer(detail.steps, requiredTime)

            // category도 함께 설정
            if (detail.category?.isNotBlank() == true && detail.category != "없음") {
                Log.d("HomeScreen", "🔄 routineDetail에서 category 설정: ${detail.category}")
                sharedViewModel.setRoutineCategory(detail.category)
            }
        }
    }

    //탭 선택 상태(오늘,이번주)
    var selectedTab by remember { mutableStateOf(0) }

    val finishedId by homeEntry.savedStateHandle
        .getStateFlow<String?>("finishedRoutineId", null)
        .collectAsState(initial = null)

    val savedOrderIds by homeEntry.savedStateHandle
        .getStateFlow<List<String>>("todayOrderIds", emptyList())
        .collectAsState(initial = emptyList())

    // 서버 응답이 들어오면: 저장된 순서(todayOrderIds)로 복원, 없으면 시간순 정렬
    LaunchedEffect(serverRoutines, savedOrderIds) {
        if (serverRoutines.isEmpty()) {
            Log.d("HomeScreen", "serverRoutines 비어있음 → 오늘 루틴 없음(서버)")
            todayRoutines.clear()
            homeEntry.savedStateHandle["todayOrderIds"] = emptyList<String>()
            return@LaunchedEffect
        }

        Log.d(
            "HomeScreen",
            "serverRoutines size=${serverRoutines.size}, savedOrderIds=${savedOrderIds.size}"
        )
        Log.d("HomeScreen", "server IDs=" + serverRoutines.joinToString { it.routineId })

        val ordered = if (savedOrderIds.isNotEmpty()) {
            val byId: Map<String, Routine> = serverRoutines.associateBy { it.routineId }
            val inSaved: List<Routine> = savedOrderIds.mapNotNull { byId[it] }
            val remaining: List<Routine> =
                serverRoutines.filter { it.routineId !in savedOrderIds.toSet() }
            inSaved + remaining
        } else {
            // 저장된 순서가 없으면 현재 시간 기준으로 가장 가까운 시간대부터 정렬
            serverRoutines.sortByNearestTime()
        }

        Log.d("HomeScreen", "ordered IDs=" + ordered.joinToString { it.routineId })

        todayRoutines.clear()
        todayRoutines.addAll(ordered)

        // 첫 진입이면 현재 순서를 저장해 둔다 (복원용)
        if (savedOrderIds.isEmpty()) {
            val ids = ordered.map { it.routineId }
            homeEntry.savedStateHandle["todayOrderIds"] = ids
            Log.d("HomeScreen", "save todayOrderIds=" + ids.joinToString())
        }
    }

    // 완료 루틴 맨 뒤로 이동 + 순서 저장
    LaunchedEffect(finishedId) {
        finishedId?.let { id ->
            Log.d(
                "HomeScreen",
                "🔄 finishedId 수신 = $id"
            )
            Log.d(
                "HomeScreen",
                "📋 현재 todayRoutines: " + todayRoutines.joinToString { "${it.title}(${it.routineId})" }
            )
            
            val idx = todayRoutines.indexOfFirst { it.routineId == id }
            Log.d("HomeScreen", "🔍 찾은 인덱스: $idx (routineId=$id)")
            
            if (idx >= 0) {
                val finished = todayRoutines.removeAt(idx)
                todayRoutines.add(finished)
                Log.d("HomeScreen", "✅ 완료된 루틴을 맨 뒤로 이동: ${finished.title}")
                Log.d(
                    "HomeScreen",
                    "📋 이동 후 todayRoutines: " + todayRoutines.joinToString { "${it.title}(${it.routineId})" }
                )
                
                // 순서 저장
                val newOrderIds = todayRoutines.map { it.routineId }
                homeEntry.savedStateHandle["todayOrderIds"] = newOrderIds
                Log.d("HomeScreen", "💾 새로운 순서 저장: " + newOrderIds.joinToString())
            } else {
                Log.w("HomeScreen", "❌ finishedId=$id 가 현재 리스트에 없음")
                Log.w("HomeScreen", "🔍 todayRoutines의 routineId들: " + todayRoutines.map { it.routineId }.joinToString())
            }
            
            // finishedId 초기화
            homeEntry.savedStateHandle["finishedRoutineId"] = null
            Log.d("HomeScreen", "🔄 finishedRoutineId 초기화 완료")
        }
    }

    // 루틴 태그 샘플(이번주 탭 선택 시 달력 날짜에 들어갈 것들) — 기존 주석/구조 유지
    val sampleRoutineTags = mapOf(
        8 to listOf("아침 운동", "회의"),
        10 to listOf("아침 운동"),
        12 to listOf("아침 운동", "회의"),
        13 to listOf("주말아침 완전집중루틴"),
        14 to listOf("주말아침루틴")
    )

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        // FAB
        floatingActionButton = {
            HomeFloatingActionButton(
                modifier = Modifier
                    .offset(y = -FabConstants.FabTotalBottomPadding)
                    .onGloballyPositioned { layoutCoordinates ->
                        val position = layoutCoordinates.positionInRoot()
                        val size = layoutCoordinates.size
                        val centerY = position.y + size.height / 2f
                        fabOffsetY.value = centerY
                    },
                onClick = { navController.navigate(Route.RoutineCreate.route) }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->

        LaunchedEffect(todayTabOffsetY.value, fabOffsetY.value) {
            if (todayTabOffsetY.value > 0f && fabOffsetY.value > 0f) {
                Log.d(
                    "HomeScreen",
                    "온보딩 트리거: todayTabY=${todayTabOffsetY.value}, fabY=${fabOffsetY.value}"
                )
                onShowOnboarding()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp), // 하단 여유 공간 추가
            verticalArrangement = Arrangement.spacedBy(8.dp) // 아이템 간 간격 추가
        ) {
            item {
                //로고와 MORU
                HomeTopAppBar()
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(111.dp)
                ) {
                    // 1.인삿말
                    val displayName = nickname ?: "XX"
                    Text(
                        text = "${displayName}님,\n오늘은 어떤 루틴을 시작할까요?",
                        style = typography.title_B_20.copy(lineHeight = 30.sp),
                        color = colors.black,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 16.dp, top = 26.dp, bottom = 25.dp)
                    )
                }
            }
            item {
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.lightGray,
                    thickness = 1.dp
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                Column(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        val boundsInRoot = coordinates.boundsInRoot()
                    }
                ) {
                    // 2. TODAY 텍스트
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val boundsInRoot = coordinates.boundsInRoot()
                            },
                        text = "TODAY",
                        style = typography.desc_M_16.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        ),
                        color = colors.black,
                    )

                    // 3. 월 일 요일
                    val currentDate = LocalDate.now()
                    val monthDay =
                        currentDate.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN))
                    val dayOfWeek = when (currentDate.dayOfWeek.value) {
                        1 -> "월"
                        2 -> "화"
                        3 -> "수"
                        4 -> "목"
                        5 -> "금"
                        6 -> "토"
                        7 -> "일"
                        else -> ""
                    }
                    val todayText = "$monthDay $dayOfWeek"
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val boundsInRoot = coordinates.boundsInRoot()
                            },
                        text = todayText,
                        style = typography.head_EB_24.copy(lineHeight = 24.sp),
                        color = colors.black
                    )

                    // 4. 상태 텍스트 (서버 오늘 루틴 기준)
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val boundsInRoot = coordinates.boundsInRoot()
                            },
                        text = if (todayRoutines.isNotEmpty()) "정기 루틴이 있는 날이에요" else "정기 루틴이 없는 날이에요",
                        style = typography.desc_M_16.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        ),
                        color = colors.black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. TodayWeekTab 래퍼 Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val boundsInRoot = coordinates.boundsInRoot()
                                val centerY = boundsInRoot.center.y

                                if (centerY > 0f) {
                                    todayTabOffsetY.value = centerY
                                }
                            }
                    ) {
                        TodayWeekTab(
                            selectedTabIndex = selectedTab,
                            onTabSelected = {
                                Log.d("HomeScreen", "탭 변경: $selectedTab -> $it")
                                selectedTab = it
                            }
                        )
                    }

                    // 선택된 탭에 따라 콘텐츠 분기
                    when (selectedTab) {
                        // 오늘 탭 선택 시
                        0 -> if (todayRoutines.isNotEmpty()) {
                            Log.d("HomeScreen", "TODAY 탭 노출, count=${todayRoutines.size}")
                            TodayRoutinePager(
                                routines = todayRoutines,
                                onRoutineClick = { routine, _ ->
                                    Log.d("HomeScreen", "🔄 Pager 루틴 클릭:")
                                    Log.d("HomeScreen", "   - routineId: ${routine.routineId}")
                                    Log.d("HomeScreen", "   - title: ${routine.title}")
                                    Log.d("HomeScreen", "   - category: ${routine.category}")
                                    Log.d("HomeScreen", "   - tags: ${routine.tags}")
                                    val stableId = routine.routineId.toStableIntId()
                                    Log.d("HomeScreen", "   - stableId: $stableId")
                                    
                                    // intro 화면을 본 적이 있는지 확인
                                    val hasSeenIntro = context.getSharedPreferences("routine_intro_prefs", android.content.Context.MODE_PRIVATE)
                                        .getBoolean("has_seen_intro_${routine.title}", false)
                                    
                                    Log.d("HomeScreen", "🔍 intro 화면 확인: ${routine.title} - hasSeenIntro=$hasSeenIntro")
                                    
                                    sharedViewModel.setSelectedRoutineId(stableId)
                                    sharedViewModel.setOriginalRoutineId(routine.routineId)
                                    Log.d("HomeScreen", "🔄 setRoutineInfo 호출")
                                    // requiredTime 기반으로 간편/집중 구분
                                    val isSimple = determineRoutineType(routine.requiredTime)
                                    val actualCategory = if (isSimple) "간편" else "집중"
                                    Log.d("HomeScreen", "📱 루틴 카테고리 설정: ${routine.title} -> $actualCategory (isSimple=$isSimple, requiredTime=${routine.requiredTime})")
                                    sharedViewModel.setRoutineInfo(title = routine.title, category = actualCategory, tags = routine.tags, isSimple = isSimple)

                                    // 루틴 상세 정보 로드 (스텝 포함) 후 SharedRoutineViewModel에 직접 설정
                                    Log.d("HomeScreen", "🔄 loadMyRoutineDetail 호출 (사용앱 정보 포함)")
                                    homeVm.loadMyRoutineDetail(routine.routineId)

                                                                    if (hasSeenIntro && isSimple) {
                                    // 이미 intro를 본 간편 루틴이면 바로 간편 루틴 화면으로 이동
                                    Log.d("HomeScreen", "🚀 이미 intro를 본 간편 루틴, 바로 간편 루틴 화면으로 이동")
                                    
                                    // 저장된 스텝 상태 복원
                                    val savedStepStatesJson = context.getSharedPreferences("routine_intro_prefs", android.content.Context.MODE_PRIVATE)
                                        .getString("saved_steps_${routine.title}", null)
                                    
                                    if (savedStepStatesJson != null) {
                                        try {
                                            val gson = com.google.gson.Gson()
                                            val type = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, RoutineStepData::class.java).type
                                            val savedStepStates: List<RoutineStepData> = gson.fromJson(savedStepStatesJson, type)
                                            
                                            // SharedRoutineViewModel에 저장된 스텝 상태 설정
                                            sharedViewModel.setStepsFromSaved(savedStepStates)
                                            Log.d("HomeScreen", "🔄 저장된 스텝 상태 복원: ${savedStepStates.size}개 스텝")
                                        } catch (e: Exception) {
                                            Log.e("HomeScreen", "❌ 저장된 스텝 상태 복원 실패", e)
                                        }
                                    }
                                    
                                    // 스택에 추가
                                    val currentRunningIds = homeEntry.savedStateHandle.get<List<Int>>("runningRoutineIds") ?: emptyList()
                                    val updatedRunningIds = currentRunningIds + stableId
                                    homeEntry.savedStateHandle["runningRoutineIds"] = updatedRunningIds
                                    Log.d("HomeScreen", "🎯 바로 간편 루틴 화면으로 이동 시 스택에 추가: $stableId (스택 크기: ${updatedRunningIds.size})")
                                    
                                    // 바로 간편 루틴 화면으로 네비게이션
                                    homeEntry.savedStateHandle["navigateToRoutineSimpleRun"] = routine.routineId
                                } else {
                                        // 처음이거나 집중 루틴이면 intro 화면으로 이동
                                        Log.d("HomeScreen", "🎯 intro 화면으로 이동 (스택 추가 안함)")
                                        homeEntry.savedStateHandle["navigateToRoutineFocus"] = routine.routineId
                                    }
                                }
                            )
                        } else {
                            // 오늘 루틴 없을 때도 Divider가 밀려 오지 않도록 고정 높이 확보
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(184.dp) // TodayRoutinePager의 전체 높이와 동일
                            )
                            Log.d("HomeScreen", "TODAY 탭이지만 todayRoutines 비어있음 → Pager 미노출")
                        }

                        // 이번주 탭 선택 시
                        1 -> {
                            // 주간 데이터 만들기 (todayRoutines 사용 - 서버 스케줄 정보가 포함됨)
                            val mergedRoutines = todayRoutines.toList()
                            
                            Log.d("HomeScreen", "🔍 이번주 탭 선택됨: mergedRoutines.size=${mergedRoutines.size}")
                            
                            // mergedRoutines 상세 정보 로깅
                            mergedRoutines.forEachIndexed { index, routine ->
                                val routineTyped: Routine = routine
                                Log.d("HomeScreen", "🔍 mergedRoutines[$index]: ${routineTyped.title}, category=${routineTyped.category}, scheduledDays=${routineTyped.scheduledDays}, scheduledTime=${routineTyped.scheduledTime}, requiredTime=${routineTyped.requiredTime}")
                            }
                            
                            val (routinesPerDate, todayDom) = buildWeeklyMap(mergedRoutines)
                            Log.d("HomeScreen", "✅ 주간 데이터 생성 완료: routinesPerDate=$routinesPerDate, todayDom=$todayDom")
                            
                            // 각 날짜별 루틴 개수 로깅
                            routinesPerDate.forEach { (date, labels) ->
                                Log.d("HomeScreen", "📅 ${date}일: ${labels.size}개 루틴 - $labels")
                            }
                            
                            WeeklyCalendarView(
                                routinesPerDate = routinesPerDate,
                                today = todayDom
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 7.dp,
                        color = colors.lightGray
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    //루틴 목록 (오늘 루틴들 그대로 노출)
                    Row(
                        modifier = Modifier.padding(top = 3.dp, start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "루틴 목록",
                            style = typography.desc_M_16.copy(fontWeight = FontWeight.Bold),
                            color = colors.black,
                            modifier = Modifier.clickable {
                                navController.navigate(Route.MyRoutine.route)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_c),
                            contentDescription = "오른쪽 화살표",
                            modifier = Modifier.size(width = 8.dp, height = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ⬇️ 하단 카드는 "내 루틴 전체" 사용 + 우선순위 정렬
                    if (myRoutines.isNotEmpty()) {
                        val context = LocalContext.current
                        val list = myRoutines.sortedForList()   // 이미 정렬된 리스트
                        
                        Log.d("HomeScreen", "🔄 하단 카드 렌더링: myRoutines.size=${myRoutines.size}, sortedList.size=${list.size}")
                        Log.d("HomeScreen", "📋 정렬된 리스트: " + list.joinToString { "${it.title}(isRunning=${it.isRunning})" })
                        Log.d("HomeScreen", "🔍 정렬된 리스트 첫 번째: ${list.firstOrNull()?.title} (isRunning=${list.firstOrNull()?.isRunning})")

                        RoutineCardList(
                            routines = list,
                            onRoutineClick = { routineId: String ->
                                Log.d("HomeScreen", "카드 클릭: id=$routineId")

                                // 정렬된 리스트에서 클릭된 루틴 찾기
                                val routine = list.firstOrNull { it.routineId == routineId }
                                if (routine == null) {
                                    Log.w("HomeScreen", "루틴 정보를 찾을 수 없습니다")
                                    return@RoutineCardList
                                }

                                // 기존 Int API와 호환
                                val stableId = routine.routineId.toStableIntId()
                                sharedViewModel.setSelectedRoutineId(stableId)
                                sharedViewModel.setOriginalRoutineId(routine.routineId)
                                
                                // intro 화면을 본 적이 있는지 확인
                                val hasSeenIntro = context.getSharedPreferences("routine_intro_prefs", android.content.Context.MODE_PRIVATE)
                                    .getBoolean("has_seen_intro_${routine.title}", false)
                                
                                Log.d("HomeScreen", "🔍 intro 화면 확인: ${routine.title} - hasSeenIntro=$hasSeenIntro")
                                // requiredTime 기반으로 간편/집중 구분
                                val isSimple = determineRoutineType(routine.requiredTime)
                                val actualCategory = if (isSimple) "간편" else "집중"
                                Log.d("HomeScreen", "📱 루틴 카테고리 설정: ${routine.title} -> $actualCategory (isSimple=$isSimple, requiredTime=${routine.requiredTime})")
                                sharedViewModel.setRoutineInfo(
                                    title = routine.title,
                                    category = actualCategory,
                                    tags = routine.tags,
                                    isSimple = isSimple
                                )

                                // 루틴 상세 정보 로드 (스텝 포함) 후 네비게이션
                                homeVm.loadMyRoutineDetail(routine.routineId)

                                if (hasSeenIntro && isSimple) {
                                    // 이미 intro를 본 간편 루틴이면 바로 간편 루틴 화면으로 이동
                                    Log.d("HomeScreen", "🚀 이미 intro를 본 간편 루틴, 바로 간편 루틴 화면으로 이동")
                                    
                                    // 저장된 스텝 상태 복원
                                    val savedStepStatesJson = context.getSharedPreferences("routine_intro_prefs", android.content.Context.MODE_PRIVATE)
                                        .getString("saved_steps_${routine.title}", null)
                                    
                                    if (savedStepStatesJson != null) {
                                        try {
                                            val gson = com.google.gson.Gson()
                                            val type = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, RoutineStepData::class.java).type
                                            val savedStepStates: List<RoutineStepData> = gson.fromJson(savedStepStatesJson, type)
                                            
                                            // SharedRoutineViewModel에 저장된 스텝 상태 설정
                                            sharedViewModel.setStepsFromSaved(savedStepStates)
                                            Log.d("HomeScreen", "🔄 저장된 스텝 상태 복원: ${savedStepStates.size}개 스텝")
                                        } catch (e: Exception) {
                                            Log.e("HomeScreen", "❌ 저장된 스텝 상태 복원 실패", e)
                                        }
                                    }
                                    
                                    // 스택에 추가
                                    val currentRunningIds = homeEntry.savedStateHandle.get<List<Int>>("runningRoutineIds") ?: emptyList()
                                    val updatedRunningIds = currentRunningIds + stableId
                                    homeEntry.savedStateHandle["runningRoutineIds"] = updatedRunningIds
                                    Log.d("HomeScreen", "🎯 바로 간편 루틴 화면으로 이동 시 스택에 추가: $stableId (스택 크기: ${updatedRunningIds.size})")
                                    
                                    // 바로 간편 루틴 화면으로 네비게이션
                                    homeEntry.savedStateHandle["navigateToRoutineSimpleRun"] = routine.routineId
                                } else {
                                    // 처음이거나 집중 루틴이면 intro 화면으로 이동
                                    Log.d("HomeScreen", "🎯 intro 화면으로 이동 (스택 추가 안함)")
                                    homeEntry.savedStateHandle["navigateToRoutineFocus"] = routine.routineId
                                }
                            },
                            runningHighlightIds = highlightIds
                        )
                    } else {
                        Log.d("HomeScreen", "내 루틴 목록이 비어있음")
                    }

                    // 하단 여유 공간 추가 (스크롤이 제대로 작동하도록)
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

// String ID → 안정적인 Int 키 (기존 Int API/콜백용)
private fun String.toStableIntId(): Int {
    this.toLongOrNull()?.let {
        val mod = (it % Int.MAX_VALUE).toInt()
        return if (mod >= 0) mod else -mod
    }
    var h = 0
    for (ch in this) h = (h * 31) + ch.code
    return h
}

// 오늘 "루틴 목록" 전용 정렬:
// 1) 진행중 루틴 우선 (스택 순서 유지) → 2) 시간 미설정 → 3) 시간 설정(오름차순)
private fun List<Routine>.sortedForList(): List<Routine> {
    Log.d("HomeScreen", "🔄 sortedForList() 호출: ${this.size}개 루틴")
    this.forEach { routine ->
        Log.d("HomeScreen", "   - ${routine.title}: isRunning=${routine.isRunning}, category=${routine.category}")
    }
    
    // 진행중인 루틴들과 나머지 루틴들을 분리
    val runningRoutines = this.filter { it.isRunning }
    val nonRunningRoutines = this.filter { !it.isRunning }
    
    // 나머지 루틴들을 기존 정렬 기준으로 정렬
    val sortedNonRunning = nonRunningRoutines.sortedWith(
        compareByDescending<Routine> { it.scheduledTime == null }
            .thenBy { it.scheduledTime ?: java.time.LocalTime.MAX }
    )
    
    // 진행중인 루틴들 + 정렬된 나머지 루틴들 (스택 순서 유지)
    val result = runningRoutines + sortedNonRunning
    
    Log.d("HomeScreen", "✅ 정렬 완료: " + result.joinToString { "${it.title}(isRunning=${it.isRunning})" })
    return result
}

// 현재 시간을 기준으로 가장 가까운 시간대의 루틴부터 정렬 (오늘 탭용)
private fun List<Routine>.sortByNearestTime(): List<Routine> {
    val now = LocalTime.now()
    return this.sortedWith(
        compareBy<Routine> { routine ->
            when {
                // 1. 진행중인 루틴 우선
                routine.isRunning -> -1
                // 2. 시간이 설정되지 않은 루틴은 맨 뒤로
                routine.scheduledTime == null -> 1
                // 3. 시간이 설정된 루틴은 현재 시간과의 차이로 정렬
                else -> {
                    val timeDiff = kotlin.math.abs(
                        java.time.Duration.between(now, routine.scheduledTime).toMinutes()
                    )
                    // 오늘 이미 지난 시간은 내일로 계산
                    val adjustedDiff = if (routine.scheduledTime < now) {
                        timeDiff + 24 * 60 // 24시간(1440분) 추가
                    } else {
                        timeDiff
                    }
                    adjustedDiff
                }
            }
        }
    )
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun HomeScreenPreview() {
    val fakeNavController = rememberNavController()
    val previewSharedViewModel = SharedRoutineViewModel()
    val previewFabOffsetY = remember { mutableStateOf(0f) }
    val todayTabOffsetY = remember { mutableStateOf(0f) }

    HomeScreen(
        navController = fakeNavController,
        sharedViewModel = previewSharedViewModel,
        fabOffsetY = previewFabOffsetY,
        todayTabOffsetY = todayTabOffsetY,
    )
}
