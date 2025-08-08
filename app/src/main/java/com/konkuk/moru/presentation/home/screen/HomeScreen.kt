package com.konkuk.moru.presentation.home.screen

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
import com.konkuk.moru.presentation.home.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun convertDurationToMinutes(duration: String): Int {
    val parts = duration.split(":")
    val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return minutes + (seconds / 60)
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
    // 서버 오늘 루틴
    val homeVm: HomeRoutinesViewModel = hiltViewModel()
    val serverRoutines by homeVm.serverRoutines.collectAsState()

    LaunchedEffect(Unit) {
        homeVm.loadTodayRoutines()
    }

    //탭 선택 상태(오늘,이번주)
    var selectedTab by remember { mutableStateOf(0) }

    // 오늘 탭 표시용(서버 응답 + 순서 복원/완료 시 뒤로)
    val todayRoutines = remember { mutableStateListOf<Routine>() }

    // 포커스 화면에서 완료한 루틴을 홈으로 돌려받아 카드 뒤로 보내기
    val homeEntry = remember(navController) {
        navController.getBackStackEntry(Route.Home.route)
    }
    val finishedId by homeEntry.savedStateHandle
        .getStateFlow<String?>("finishedRoutineId", null)
        .collectAsState(initial = null)

    val savedOrderIds by homeEntry.savedStateHandle
        .getStateFlow<List<String>>("todayOrderIds", emptyList())
        .collectAsState(initial = emptyList())

    // 서버 응답이 들어오면: 저장된 순서(todayOrderIds)로 복원, 없으면 서버 순서 그대로 사용
    LaunchedEffect(serverRoutines, savedOrderIds) {
        if (serverRoutines.isEmpty()) return@LaunchedEffect

        val ordered = if (savedOrderIds.isNotEmpty()) {
            val byId = serverRoutines.associateBy { it.routineId }
            val inSaved = savedOrderIds.mapNotNull { byId[it] }
            val remaining = serverRoutines.filter { it.routineId !in savedOrderIds.toSet() }
            inSaved + remaining
        } else {
            serverRoutines // 서버가 TIME + dayOfWeek로 내려줌
        }

        todayRoutines.clear()
        todayRoutines.addAll(ordered)

        // 첫 진입이면 현재 순서를 저장해 둔다 (복원용)
        if (savedOrderIds.isEmpty()) {
            homeEntry.savedStateHandle["todayOrderIds"] = ordered.map { it.routineId }
        }
    }

    LaunchedEffect(finishedId) {
        finishedId?.let { id ->
            // 1) 현재 보여주는 todayRoutines에서 해당 카드 찾아 제거
            val idx = todayRoutines.indexOfFirst { it.routineId == id }
            if (idx >= 0) {
                val finished = todayRoutines.removeAt(idx)
                // 2) 맨 뒤로 추가
                todayRoutines.add(finished)
            }
            // 3) 순서 저장 + 중복 트리거 방지
            homeEntry.savedStateHandle["todayOrderIds"] = todayRoutines.map { it.routineId }
            homeEntry.savedStateHandle["finishedRoutineId"] = null
        }
    }

    // 루틴 태그 샘플(이번주 탭 선택 시 달력 날짜에 들어갈 것들)
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
            // 두 값이 모두 측정되었을 때만 온보딩 시작
            if (todayTabOffsetY.value > 0f && fabOffsetY.value > 0f) {
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
                    Text(
                        text = "XX님,\n오늘은 어떤 루틴을 시작할까요?",
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

                    // 4. 상태 텍스트
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
                            onTabSelected = { selectedTab = it }
                        )
                    }

                    // 선택된 탭에 따라 콘텐츠 분기
                    when (selectedTab) {
                        // 오늘 탭 선택 시
                        0 -> if (todayRoutines.isNotEmpty()) {
                            TodayRoutinePager(
                                routines = todayRoutines,
                                onRoutineClick = { routine, _ ->
                                    // Step 리스트 변환
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

                                    // 루틴 기본 정보 설정
                                    sharedViewModel.setRoutineInfo(
                                        title = routine.title,
                                        category = routine.category,
                                        tags = routine.tags
                                    )

                                    // 네비게이션 (String ID 사용)
                                    navController.navigate(Route.RoutineFocusIntro.route)
                                }
                            )
                        }

                        // 이번주 탭 선택 시 (샘플)
                        1 -> {
                            WeeklyCalendarView(
                                routinesPerDate = mapOf(
                                    8 to listOf("아침 운동", "회의"),
                                    10 to listOf("아침 운동"),
                                    12 to listOf("아침 운동", "회의"),
                                    13 to listOf("주말아침 완전집중루틴"),
                                    14 to listOf("주말아침루틴")
                                ),
                                today = 13
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 7.dp,
                        color = colors.lightGray
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    // 루틴 목록 (오늘 루틴들 그대로 노출)
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

                    if (todayRoutines.isNotEmpty()) {
                        val context = LocalContext.current

                        val myRoutines = todayRoutines

                        RoutineCardList(
                            routines = myRoutines,
                            onRoutineClick = { routineId -> // routineId: String (RoutineCardList가 String 콜백이어야 함)
                                val routine = myRoutines.find { it.routineId == routineId }
                                if (routine != null) {
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

                                    navController.navigate("routine_focus_intro/${routine.routineId}")
                                } else {
                                    Toast.makeText(context, "루틴 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
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
