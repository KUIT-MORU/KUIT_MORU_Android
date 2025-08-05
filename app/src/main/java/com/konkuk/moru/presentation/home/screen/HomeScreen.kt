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
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.R
import com.konkuk.moru.data.model.DummyData
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
    //탭 선택 상태(오늘,이번주)
    var selectedTab by remember { mutableStateOf(0) }

    // 루틴 샘플 데이터(오늘 탭 선택 시 보여줄 박스의 내용들)
    val sampleRoutines = DummyData.feedRoutines.filter { it.routineId in listOf(501, 502, 503, 504) }

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
        floatingActionButtonPosition = FabPosition.End, // ← 이걸 추가
    ) { innerPadding ->

        val isTodayTabMeasured = remember { mutableStateOf(false) }

        LaunchedEffect(todayTabOffsetY.value, fabOffsetY.value) {
            // 두 값이 모두 측정되었을 때만 온보딩 시작
            if (todayTabOffsetY.value > 0f && fabOffsetY.value > 0f) {
                onShowOnboarding()
            } else {
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
//            item {
//                // 상단 상태 바
//                StatusBarMock(isDarkMode = true)
//            }
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
                        style = typography.title_B_20.copy(
                            lineHeight = 30.sp
                        ),
                        color = colors.black,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = 16.dp,
                                top = 26.dp,
                                bottom = 25.dp
                            )
                    )
                }
            }
            item {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = colors.lightGray,
                    thickness = 1.dp
                )
            }
            item {
                Spacer(Modifier.height(8.dp))
            }
            item {
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
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
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val boundsInRoot = coordinates.boundsInRoot()
                            },
                        text = "5월 10일 토",
                        style = typography.head_EB_24.copy(
                            lineHeight = 24.sp
                        ),
                        color = colors.black
                    )

                    // 4. 상태 텍스트
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val boundsInRoot = coordinates.boundsInRoot()
                            },
                        text = if (sampleRoutines.isNotEmpty()) "정기 루틴이 있는 날이에요" else "정기 루틴이 없는 날이에요",
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
                        // 원래 TodayWeekTab 사용
                        TodayWeekTab(
                            selectedTabIndex = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }


                    // 선택된 탭에 따라 콘텐츠 분기
                    when (selectedTab) {
                        // 오늘 탭 선택 시
                        0 -> if (sampleRoutines.isNotEmpty()) {
                            TodayRoutinePager(
                                routines = sampleRoutines,
                                onRoutineClick = { routine, index ->
                                    // Step 리스트 변환
                                    val stepDataList = routine.steps.map {
                                        RoutineStepData(
                                            name = it.name,
                                            duration = convertDurationToMinutes(it.duration),
                                            isChecked = false
                                        )
                                    }
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
                        }

                        // 이번주 탭 선택 시
                        1 -> {
                            WeeklyCalendarView(
                                routinesPerDate = sampleRoutineTags,
                                today = 13
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        thickness = 7.dp,
                        color = colors.lightGray
                    )
                    //루틴 목록
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
                            modifier = Modifier
                                .size(width = 8.dp, height = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // 후에 실제 데이터로 오늘 루틴이 있는지 확인
                    if (sampleRoutines.isNotEmpty()) {
                        val context = LocalContext.current

                        // 내 루틴만 필터링
                        val myRoutineIds = listOf(501, 502, 503, 504)
                        val myRoutines = DummyData.feedRoutines.filter { it.routineId in listOf(501, 502, 503, 504) }

                        RoutineCardList(
                            routines = myRoutines,
                            onRoutineClick = { routineId ->
                                val routine = myRoutines.find { it.routineId == routineId }
                                if (routine != null) {
                                    val stepDataList = routine.steps.map {
                                        RoutineStepData(
                                            name = it.name,
                                            duration = convertDurationToMinutes(it.duration),
                                            isChecked = false
                                        )
                                    }

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

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun HomeScreenPreview() {
    val fakeNavController = rememberNavController()
    val previewSharedViewModel = SharedRoutineViewModel()
    val previewFabOffsetY = remember { mutableStateOf(0f) } // 🔹 추가
    val todayTabOffsetY = remember { mutableStateOf(0f) } // 🔹 추가

    HomeScreen(
        navController = fakeNavController,
        sharedViewModel = previewSharedViewModel,
        fabOffsetY = previewFabOffsetY,
        todayTabOffsetY = todayTabOffsetY,
    )
}