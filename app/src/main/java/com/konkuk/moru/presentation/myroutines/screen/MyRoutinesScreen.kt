package com.konkuk.moru.presentation.myroutines.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.core.component.routine.RoutineListItemWithClock
import com.konkuk.moru.presentation.routinefeed.component.modale.TimePickerSheetContent
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.MyRoutineTopAppBar
import com.konkuk.moru.presentation.routinefeed.component.tooltip.TooltipBubble
import com.konkuk.moru.presentation.routinefeed.component.tooltip.TooltipShape
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyRoutinesScreen(
    modifier: Modifier = Modifier,
    routines: List<MyRoutine>,
    onConfirmTimeSet: (Int, LocalTime, Set<DayOfWeek>, Boolean) -> Unit,
    onNavigateToCreateRoutine: () -> Unit,
    onNavigateToRoutineFeed: () -> Unit,
    onInfoClick: () -> Unit,
    onTrashClick: () -> Unit
) {
    var selectedSortOption by remember { mutableStateOf("시간순") }
    val sortOptions = listOf("시간순", "최신순", "인기순")
    var showInfoTooltip by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showTimePickerSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var selectedRoutineForTimeSet by remember { mutableStateOf<MyRoutine?>(null) }

    val likedStates = remember(routines) { mutableStateMapOf<Int, Boolean>().apply { routines.forEach { put(it.id, it.isLiked) } } }
    val likeCounts = remember(routines) { mutableStateMapOf<Int, Int>().apply { routines.forEach { put(it.id, it.likes) } } }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyRoutineTopAppBar(
                    onInfoClick = { showInfoTooltip = true },
                    onTrashClick = onTrashClick,
                    onDaySelected = { dayIndex ->
                        println("Selected day index: $dayIndex")
                    }
                )
            },
            containerColor = Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                if (routines.isEmpty()) {
                    EmptyMyRoutineView(
                        onNavigateToCreateRoutine = onNavigateToCreateRoutine,
                        onNavigateToRoutineFeed = onNavigateToRoutineFeed
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sortOptions.forEach { option ->
                            MoruChip(
                                text = option,
                                isSelected = selectedSortOption == option,
                                onClick = { selectedSortOption = option },
                                selectedBackgroundColor = Color(0xFF555555),
                                selectedContentColor = Color.White,
                                unselectedBackgroundColor = Color(0xFFF0F0F0),
                                unselectedContentColor = Color(0xFF888888)
                            )
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = routines, key = { it.id }) { routine ->
                            val isLiked = likedStates[routine.id] ?: false
                            val currentLikeCount = likeCounts[routine.id] ?: 0

                            RoutineListItemWithClock(
                                isRunning = routine.isRunning,
                                routineName = routine.name,
                                tags = routine.tags,
                                likeCount = currentLikeCount,
                                isLiked = isLiked,
                                onLikeClick = {
                                    val newLikeStatus = !isLiked
                                    likedStates[routine.id] = newLikeStatus
                                    likeCounts[routine.id] = if (newLikeStatus) currentLikeCount + 1 else currentLikeCount - 1
                                },
                                onClockClick = {
                                    selectedRoutineForTimeSet = routine
                                    showTimePickerSheet = true
                                }
                            )
                        }
                    }
                }
            }
        } // Scaffold 끝

        if (showInfoTooltip) {
            LaunchedEffect(Unit) {
                delay(3000L)
                showInfoTooltip = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp, end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                TooltipBubble(
                    modifier = Modifier.width(328.dp),
                    shape = TooltipShape(cornerRadius = 8.dp, tailHorizontalOffsetFromEnd = 38.dp),
                    backgroundColor = Color.Black,
                    tailHeight = 11.5.dp
                ) {
                    Text(
                        text = "시간대가 설정되지 않은 루틴은 인사이트 분석 시에 제외됩니다.\n정확한 인사이트를 원한다면 시간대를 설정해보세요!",
                        color = Color.White,
                        fontFamily = moruFontLight,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
        }
    } // 최상위 Box 끝

    if (showTimePickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimePickerSheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = Color.White
        ) {
            // 데모용 TimeSet()을 호출하는 것이 아니라, 알맹이인 TimePickerSheetContent()를 사용합니다.
            TimePickerSheetContent(
                onConfirm = { time, days, alarm ->
                    // 확인 버튼 클릭 시, ViewModel로 데이터를 전달하는 로직이 올바르게 연결됩니다.
                    selectedRoutineForTimeSet?.let {
                        onConfirmTimeSet(it.id, time, days, alarm)
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showTimePickerSheet = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyMyRoutineView(
    onNavigateToCreateRoutine: () -> Unit,
    onNavigateToRoutineFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_empty_routine_box),
            contentDescription = "루틴 없음",
            modifier = Modifier.size(81.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "아직 내 루틴이 비어있어요.",
            style = MORUTheme.typography.desc_M_20,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "당신만의 루틴을 직접 만들거나,\n다른 사람의 루틴을 참고해보세요!",
            style = MORUTheme.typography.desc_M_16,
            color = MORUTheme.colors.mediumGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "루틴 만들기",
            style = MORUTheme.typography.body_SB_16,
            color = Color(0xFF407196),
            modifier = Modifier.clickable { onNavigateToCreateRoutine() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "루틴피드 보기",
            style = MORUTheme.typography.body_SB_14,
            color = Color(0xFF407196),
            modifier = Modifier.clickable { onNavigateToRoutineFeed() }
        )
    }
}

// --- Previews ---

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "내 루틴 - 비어있을 때")
@Composable
private fun MyRoutineScreenEmptyPreview() {
    MORUTheme {
        MyRoutinesScreen(
            routines = emptyList(),
            onConfirmTimeSet = { _, _, _, _ -> },
            onNavigateToCreateRoutine = {},
            onNavigateToRoutineFeed = {},
            onInfoClick = {},
            onTrashClick = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "내 루틴 - 목록이 있을 때")
@Composable
private fun MyRoutineScreenWithContentPreview() {
    val sampleRoutines = remember {
        listOf(
            MyRoutine(1, "아침 운동", listOf("#모닝루틴", "#스트레칭"), 16, true, false),
            MyRoutine(2, "오전 명상", listOf("#마음챙김", "#집중"), 25, false, true)
        )
    }
    MORUTheme {
        MyRoutinesScreen(
            routines = sampleRoutines,
            onConfirmTimeSet = { _, _, _, _ -> },
            onNavigateToCreateRoutine = {},
            onNavigateToRoutineFeed = {},
            onInfoClick = {},
            onTrashClick = {}
        )
    }
}