package com.konkuk.moru.presentation.home.screen

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

fun convertDurationToMinutes(duration: String): Int {
    val parts = duration.split(":")
    val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return minutes + (seconds / 60)
}

// 라벨 포맷(짧게)
private fun Routine.toCalendarLabel(): String =
    this.tags.firstOrNull() ?: this.title

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
        val labels = routines
            .filter { r ->
                // 🔸 요일 세팅된 루틴만 주간에 배치
                val hasScheduledDays = r.scheduledDays.isNotEmpty()
                val containsDayOfWeek = r.scheduledDays.contains(date.dayOfWeek)
                
                // 임시 해결책: scheduledDays가 비어있으면 오늘 요일로 설정
                val shouldShow = if (!hasScheduledDays) {
                    // scheduledDays가 비어있으면 오늘 요일인 경우에만 표시
                    date.dayOfWeek == today.dayOfWeek
                } else {
                    containsDayOfWeek
                }
                
                Log.d("HomeScreen", "날짜 ${date.dayOfMonth}(${date.dayOfWeek}): ${r.title} - scheduledDays=${r.scheduledDays}, hasScheduledDays=$hasScheduledDays, containsDayOfWeek=$containsDayOfWeek, shouldShow=$shouldShow")
                shouldShow
            }
            .sortedBy { it.scheduledTime ?: LocalTime.MAX }
            .map { it.toCalendarLabel() }

        Log.d("HomeScreen", "날짜 ${date.dayOfMonth}에 표시될 라벨: $labels")
        date.dayOfMonth to labels
    }

    Log.d("HomeScreen", "최종 주간 맵: $map")
    return map to today.dayOfMonth
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
    val userVm: UserViewModel = hiltViewModel()
    val nickname by userVm.nickname.collectAsState()
    LaunchedEffect(Unit) { userVm.loadMe() }

    // Context 가져오기
    val context = LocalContext.current

    // 오늘 탭 표시용(서버 응답 + 순서 복원/완료 시 뒤로)
    val todayRoutines = remember { mutableStateListOf<Routine>() }

    // 포커스 화면에서 완료한 루틴을 홈으로 돌려받아 카드 뒤로 보내기
    val homeEntry = remember(navController) {
        navController.getBackStackEntry(Route.Home.route)
    }

    // 진행중 루틴 ID 수신 (Int 안정 ID)
    val runningId by homeEntry.savedStateHandle
        .getStateFlow<Int?>("runningRoutineId", null)
        .collectAsState(initial = null)

    // 하이라이트 대상 보관
    var highlightId by remember { mutableStateOf<Int?>(null) }

    // X 눌러서 나온 "진행중" 루틴을 맨 앞으로, isRunning=true, 하이라이트 지정
    LaunchedEffect(runningId) {
        runningId?.let { id ->
            val idx = todayRoutines.indexOfFirst { it.routineId.toStableIntId() == id }
            if (idx >= 0) {
                val item = todayRoutines.removeAt(idx)
                val updated = item.copy(isRunning = true) // 정렬에서도 앞으로 오도록
                todayRoutines.add(0, updated)
                // 순서 저장
                homeEntry.savedStateHandle["todayOrderIds"] = todayRoutines.map { it.routineId }
                // 하이라이트 지정
                highlightId = id
            }
            // 한 번 처리했으면 플래그 비워주기
            homeEntry.savedStateHandle["runningRoutineId"] = null
        }
    }

    // 서버 오늘 루틴
    val homeVm: HomeRoutinesViewModel = hiltViewModel()

    // ① Today(오늘용)
    val serverRoutines by homeVm.serverRoutines.collectAsState()
    // ② 내 루틴 전체(하단 카드용)
    val myRoutines by homeVm.myRoutines.collectAsState()
    // ③ 스케줄 정보가 병합된 루틴 (주간 달력용)
    val scheduledRoutines by homeVm.scheduledRoutines.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "loadTodayRoutines() 호출")
        homeVm.loadTodayRoutines()
        // 하단 카드용 전체 목록도 로드
        homeVm.loadMyRoutines()
    }

    // 서버 데이터 로드 후 로컬 스케줄 정보와 병합
    LaunchedEffect(serverRoutines) {
        if (serverRoutines.isNotEmpty()) {
            Log.d("HomeScreen", "서버 데이터 로드 완료, 로컬 스케줄 정보와 병합 시작")
            homeVm.mergeWithLocalSchedule(context)
            
            // 테스트용: 임시로 스케줄 데이터 설정 (실제로는 시계 아이콘을 통해 설정)
            if (serverRoutines.isNotEmpty()) {
                val firstRoutine = serverRoutines.first()
                val testSchedule = SchedulePreference.ScheduleInfo(
                    routineId = firstRoutine.routineId,
                    scheduledDays = SchedulePreference.dayOfWeeksToStrings(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
                    scheduledTime = SchedulePreference.localTimeToString(LocalTime.of(9, 0))
                )
                SchedulePreference.saveSchedule(context, testSchedule)
                Log.d("HomeScreen", "테스트 스케줄 설정: ${firstRoutine.title} - ${testSchedule.scheduledDays}, ${testSchedule.scheduledTime}")
                
                // 스케줄 정보 다시 병합
                homeVm.mergeWithLocalSchedule(context)
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

    // 서버 응답이 들어오면: 저장된 순서(todayOrderIds)로 복원, 없으면 서버 순서 그대로 사용
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
            serverRoutines // 서버가 TIME + dayOfWeek로 내려줌
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
                "finishedId 수신 = $id, beforeOrder=" + todayRoutines.joinToString { it.routineId })
            val idx = todayRoutines.indexOfFirst { it.routineId == id }
            if (idx >= 0) {
                val finished = todayRoutines.removeAt(idx)
                todayRoutines.add(finished)
                Log.d("HomeScreen", "afterOrder=" + todayRoutines.joinToString { it.routineId })
            } else {
                Log.w("HomeScreen", "finishedId=$id 가 현재 리스트에 없음")
            }
            homeEntry.savedStateHandle["todayOrderIds"] = todayRoutines.map { it.routineId }
            homeEntry.savedStateHandle["finishedRoutineId"] = null
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
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                                    Log.d(
                                        "HomeScreen",
                                        "Pager 클릭: id=${routine.routineId}, title=${routine.title}"
                                    )
                                    // Step 리스트 변환
                                    val stepDataList = routine.steps.map {
                                        RoutineStepData(
                                            name = it.name,
                                            duration = convertDurationToMinutes(it.duration),
                                            isChecked = false
                                        )
                                    }
                                    // 기존 Int API와 호환 (내부 저장용 키만 변환)
                                    sharedViewModel.setSelectedRoutineId(routine.routineId.toStableIntId())
                                    sharedViewModel.setSelectedSteps(stepDataList)

                                    // 루틴 기본 정보 설정
                                    sharedViewModel.setRoutineInfo(
                                        title = routine.title,
                                        category = routine.category,
                                        tags = routine.tags
                                    )

                                    // 네비게이션
                                    navController.navigate(Route.RoutineFocusIntro.route)
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

                        // 이번주 탭 선택 시 (샘플)
                        1 -> {
                            // 주간 데이터 만들기 (scheduledRoutines 사용 - 스케줄 정보 포함)
                            Log.d("HomeScreen", "이번주 탭 선택됨: scheduledRoutines.size=${scheduledRoutines.size}")
                            val (routinesPerDate, todayDom) = buildWeeklyMap(scheduledRoutines)
                            Log.d("HomeScreen", "주간 데이터 생성 완료: routinesPerDate=$routinesPerDate, todayDom=$todayDom")
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

                        RoutineCardList(
                            routines = list,
                            onRoutineClick = { routineId: String ->
                                Log.d("HomeScreen", "카드 클릭: id=$routineId")

                                // 정렬된 리스트에서 클릭된 루틴 찾기
                                val routine = list.firstOrNull { it.routineId == routineId }
                                if (routine == null) {
                                    Toast.makeText(context, "루틴 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                                    return@RoutineCardList
                                }

                                val stepDataList = routine.steps.map {
                                    RoutineStepData(
                                        name = it.name,
                                        duration = convertDurationToMinutes(it.duration),
                                        isChecked = false
                                    )
                                }

                                // 기존 Int API와 호환
                                sharedViewModel.setSelectedRoutineId(routine.routineId.toStableIntId())
                                sharedViewModel.setSelectedSteps(stepDataList)
                                sharedViewModel.setRoutineInfo(
                                    title = routine.title,
                                    category = routine.category,
                                    tags = routine.tags
                                )

                                navController.navigate(Route.RoutineFocusIntro.route)
                            },
                            runningHighlightId = highlightId?.takeIf { id ->
                                list.any { it.routineId.toStableIntId() == id }
                            }
                        )
                    } else {
                        Log.d("HomeScreen", "내 루틴 목록이 비어있음")
                    }

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
// 1) 진행중(간편) 우선 → 2) 시간 미설정 → 3) 시간 설정(오름차순)
private fun List<Routine>.sortedForList(): List<Routine> =
    this.sortedWith(
        compareByDescending<Routine> { it.isRunning && it.category == "간편" }
            .thenByDescending { it.scheduledTime == null }
            .thenBy { it.scheduledTime ?: java.time.LocalTime.MAX }
    )

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
