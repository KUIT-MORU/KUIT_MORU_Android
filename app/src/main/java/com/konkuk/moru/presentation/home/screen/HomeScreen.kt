package com.konkuk.moru.presentation.home.screen

import android.R.attr.centerY
import android.R.attr.y
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.R
import com.konkuk.moru.core.component.Switch.StatusBarMock
import com.konkuk.moru.presentation.home.FabConstants
import com.konkuk.moru.presentation.home.FocusType
import com.konkuk.moru.presentation.home.component.HomeFloatingActionButton
import com.konkuk.moru.presentation.home.component.HomeTopAppBar
import com.konkuk.moru.presentation.home.component.HomeTutorialOverlayContainer
import com.konkuk.moru.presentation.home.component.RoutineCardList
import com.konkuk.moru.presentation.home.component.RoutineData
import com.konkuk.moru.presentation.home.component.TodayRoutinePager
import com.konkuk.moru.presentation.home.component.TodayWeekTab
import com.konkuk.moru.presentation.home.component.WeeklyCalendarView
import com.konkuk.moru.presentation.home.screen.OnboardingScreen
import com.konkuk.moru.presentation.home.viewmodel.SharedRoutineViewModel
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import kotlinx.coroutines.delay

// 홈 메인 페이지
@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedRoutineViewModel,
    modifier: Modifier = Modifier,
    fabOffsetY: MutableState<Float>,
    todayTabOffsetY: MutableState<Float>,
    onShowOnboarding: () -> Unit = {},
    onShowOverlay: () -> Unit = {},
    onDismissOverlay: () -> Unit = {}
) {

    //탭 선택 상태(오늘,이번주)
    var selectedTab by remember { mutableStateOf(0) }

    // 루틴 샘플 데이터(오늘 탭 선택 시 보여줄 박스의 내용들)
    val sampleRoutines = listOf(
        RoutineData("주말 아침 루틴", "#화이팅", 25, "토일", "am 09:00 ~ am 09:58"),
        RoutineData("출근 준비 루틴", "#힘내자", 41, "월", "am 08:00 ~ am 08:45"),
        RoutineData("운동 루틴", "#건강", 12, "수", "pm 06:00 ~ pm 07:00")
    )

    //루틴 태그 샘플(이번주 탭 선택 시 달력 날짜에 들어갈 것들)
    val sampleRoutineTags = mapOf(
        8 to listOf("아침 운동", "회의"),
        10 to listOf("아침 운동"),
        12 to listOf("아침 운동", "회의"),
        13 to listOf("주말아침루틴"),
        14 to listOf("주말아침루틴")
    )

    LaunchedEffect(Unit) {
        onShowOnboarding()
    }
    Scaffold(
        containerColor = Color.White,
        //FAB

        floatingActionButton = {
            HomeFloatingActionButton(
                modifier = Modifier
                    .offset(y = -FabConstants.FabTotalBottomPadding)
                    .onGloballyPositioned { layoutCoordinates ->
                        val position = layoutCoordinates.positionInRoot()
                        val size = layoutCoordinates.size
                        val centerY = position.y + size.height / 2f
                        fabOffsetY.value = centerY
                        Log.d("FAB_POSITION", "FAB Y Offset: $centerY")
                    },
                onClick = { /* 클릭 처리 */ }
            )
        },
        floatingActionButtonPosition = FabPosition.End, // ← 이걸 추가
    ) { innerPadding ->
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
                            .align(Alignment.TopStart)                  // Box 안의 좌상단
                            .padding(                                   // ← 내용 여백
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
                Column() {
                    // 2. Today
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
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
                            .padding(horizontal = 16.dp),
                        text = "5월 10일 토",
                        style = typography.head_EB_24.copy(
                            lineHeight = 24.sp
                        ),
                        color = colors.black
                    )
                    // 후에 실제 데이터로 오늘 루틴이 있는지 확인
                    if (sampleRoutines.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = "정기 루틴이 있는 날이에요",
                            style = typography.desc_M_16.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            ),
                            color = colors.black
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            text = "정기 루틴이 없는 날이에요",
                            style = typography.desc_M_16.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            ),
                            color = colors.black
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // 4. 탭 선택
                    // 후에 실제 데이터로 오늘 루틴이 있는지 확인
                    TodayWeekTab(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInRoot()
                                val size = coordinates.size
                                val centerY = position.y + size.height / 2f

                                Log.d("TODAY_TAB_POSITION", "TodayTab CenterY: $centerY")

                                todayTabOffsetY.value = centerY
                            },
                        selectedTabIndex = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                    // 선택된 탭에 따라 콘텐츠 분기
                    when (selectedTab) {
                        // 오늘 탭 선택 시
                        0 -> if (sampleRoutines.isNotEmpty()) {
                            TodayRoutinePager(
                                routines = sampleRoutines,
                                onRoutineClick = {
                                    //Focus 타입에 따라 뜨는 intro화면이 다름
                                    sharedViewModel.setFocusType(FocusType.FOCUS)
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
                            color = colors.black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_arrow_c),
                            contentDescription = "오른쪽 화살표",
                            modifier = Modifier
                                .size(width = 8.dp, height = 12.dp)
                        )
                    }
                    // 후에 실제 데이터로 오늘 루틴이 있는지 확인
                    if (sampleRoutines.isNotEmpty()) {
                        RoutineCardList()
                    }
                }
            }
        }

//        var showOnboarding by remember { mutableStateOf(true) }
//        var showOverlay by remember { mutableStateOf(false) }
//
//        when {
//            showOnboarding -> {
//                // 온보딩 화면 1
//                OnboardingScreen(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .zIndex(2f),
//                    onNextClick = {
//                        showOnboarding = false
//                        showOverlay = true
//                    },
//                    onCloseClick = {
//                        // 온보딩 건너뛰기 시 모든 튜토리얼 종료
//                        showOnboarding = false
//                        showOverlay = false
//                    }
//                )
//            }
//
//            showOverlay -> {
//                // 온보딩 화면 2 (튜토리얼 오버레이)
//                HomeTutorialOverlayContainer(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .zIndex(2f),
//                    onDismiss = {
//                        showOverlay = false
//                    },
//                    onFabClick = {
//                        // FAB 클릭 시 튜토리얼 종료 (또는 다음 단계)
//                        showOverlay = false
//                    },
//                    fabOffsetY = fabOffsetY.value
//                )
//            }
//        }
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
        todayTabOffsetY = todayTabOffsetY
    )
}