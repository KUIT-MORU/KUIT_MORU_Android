package com.konkuk.moru.presentation.myroutines.screen

import TimePickerSheetContent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.core.component.chip.MoruChip
import com.konkuk.moru.core.component.routine.RoutineListItem
import com.konkuk.moru.core.component.routine.RoutineListItemWithClock
import com.konkuk.moru.data.model.Routine
import com.konkuk.moru.presentation.routinefeed.component.modale.CenteredInfoDialog
import com.konkuk.moru.presentation.routinefeed.component.modale.CustomDialog
import com.konkuk.moru.presentation.routinefeed.component.tooltip.TooltipBubble
import com.konkuk.moru.presentation.routinefeed.component.tooltip.TooltipShape
import com.konkuk.moru.presentation.routinefeed.component.topAppBar.MyRoutineTopAppBar
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime

enum class SortOption { BY_TIME, LATEST, POPULAR }

data class MyRoutinesUiState(
    val selectedSortOption: SortOption = SortOption.BY_TIME,
    val selectedDay: DayOfWeek? = null,
    val isDeleteMode: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showInfoTooltip: Boolean = false,
    val editingRoutineId: Int? = null,
    val showDeleteSuccessDialog: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutinesScreen(
    modifier: Modifier = Modifier,
    uiState: MyRoutinesUiState,
    routinesToDisplay: List<Routine>, // [수정] MyRoutine -> Routine
    onSortOptionSelected: (SortOption) -> Unit,
    onDaySelected: (DayOfWeek?) -> Unit,
    onTrashClick: () -> Unit,
    onCheckRoutine: (Int, Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDeleteSuccessDialog: () -> Unit,
    onOpenTimePicker: (Int) -> Unit,
    onCloseTimePicker: () -> Unit,
    onConfirmTimeSet: (Int, LocalTime, Set<DayOfWeek>, Boolean) -> Unit,
    onLikeClick: (Int) -> Unit,
    onShowInfoTooltip: () -> Unit,
    onDismissInfoTooltip: () -> Unit,
    onNavigateToCreateRoutine: () -> Unit,
    onNavigateToRoutineFeed: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val checkedRoutinesCount = routinesToDisplay.count { it.isChecked }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MyRoutineTopAppBar(
                    onInfoClick = onShowInfoTooltip,
                    onTrashClick = onTrashClick,
                    selectedDay = uiState.selectedDay,
                    onDaySelected = { day -> onDaySelected(day) }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortOption.values().forEach { option ->
                        MoruChip(
                            text = when (option) {
                                SortOption.BY_TIME -> "시간순"
                                SortOption.LATEST -> "최신순"
                                SortOption.POPULAR -> "인기순"
                            },
                            isSelected = uiState.selectedSortOption == option,
                            onClick = { onSortOptionSelected(option) },
                            selectedBackgroundColor = Color(0xFF555555),
                            selectedContentColor = Color.White,
                            unselectedBackgroundColor = Color(0xFFF0F0F0),
                            unselectedContentColor = Color(0xFF888888)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (uiState.isDeleteMode) {
                        MoruButton(
                            text = "삭제하기",
                            textStyle = MORUTheme.typography.desc_M_12,
                            onClick = onDeleteClick,
                            enabled = checkedRoutinesCount > 0,
                            backgroundColor = MORUTheme.colors.limeGreen,
                            contentColor = Color.Black
                        )
                    }
                }

                if (routinesToDisplay.isEmpty()) {
                    EmptyMyRoutineView(onNavigateToCreateRoutine, onNavigateToRoutineFeed)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = routinesToDisplay, key = { it.routineId }) { routine ->
                            if (uiState.isDeleteMode) {
                                RoutineListItem(
                                    isRunning = routine.isRunning,
                                    routineName = routine.title, // [수정] name -> title
                                    tags = routine.tags,
                                    likeCount = routine.likes,
                                    isLiked = routine.isLiked,
                                    showCheckbox = true,
                                    isChecked = routine.isChecked,
                                    onCheckedChange = { isChecked ->
                                        onCheckRoutine(routine.routineId, isChecked)
                                    },
                                    onLikeClick = { onLikeClick(routine.routineId) },
                                    onItemClick = { onCheckRoutine(routine.routineId, !routine.isChecked) }
                                )
                            } else {
                                RoutineListItemWithClock(
                                    isRunning = routine.isRunning,
                                    routineName = routine.title, // [수정] name -> title
                                    tags = routine.tags,
                                    likeCount = routine.likes,
                                    isLiked = routine.isLiked,
                                    onLikeClick = { onLikeClick(routine.routineId) },
                                    onClockClick = { onOpenTimePicker(routine.routineId) },
                                    onItemClick = {onNavigateToDetail(routine.routineId)}
                                )
                            }
                        }
                    }
                }
            }
        }

        if (uiState.showInfoTooltip) {
            LaunchedEffect(Unit) {
                delay(3000L)
                onDismissInfoTooltip()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 118.dp, end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                TooltipBubble(
                    modifier = Modifier.width(328.dp),
                    shape = TooltipShape(cornerRadius = 8.dp, tailHorizontalOffsetFromEnd = 38.dp),
                    backgroundColor = Color.Black,
                    tailHeight = 11.5.dp
                ) {
                    Text(
                        text = "시간대가 설정되지 않은 루틴은 인사이트 분석 시에 제외됩니다.정확한 인사이트를 원한다면 시간대를 설정해보세요!",
                        color = Color.White,
                        fontFamily = moruFontLight,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (uiState.editingRoutineId != null) {
        ModalBottomSheet(
            onDismissRequest = onCloseTimePicker,
            sheetState = sheetState
        ) {
            TimePickerSheetContent(
                onConfirm = { time, days, alarm ->
                    onConfirmTimeSet(uiState.editingRoutineId, time, days, alarm)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onCloseTimePicker()
                        }
                    }
                }
            )
        }
    }

    if (uiState.showDeleteDialog) {
        CustomDialog(
            onDismissRequest = onDismissDeleteDialog,
            onConfirmation = onConfirmDelete,
            content = {
                Text(
                    text = "루틴을 삭제하시겠습니까?",
                    style = MORUTheme.typography.title_B_20,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
    if (uiState.showDeleteSuccessDialog) {
        LaunchedEffect(Unit) {
            delay(2000L)
            onDismissDeleteSuccessDialog()
        }

        CenteredInfoDialog(
            onDismissRequest = onDismissDeleteSuccessDialog,
            dialogColor = Color(0xFF212120)
        ) {
            Text(
                text = "삭제되었습니다!",
                color = Color.White,
                style = MORUTheme.typography.desc_M_14
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
@Preview(showBackground = true, name = "내 루틴 - 기본 모드")
@Composable
private fun MyRoutinesScreenPreview() {
    // [수정] Preview용 샘플 데이터를 새로운 Routine 클래스로 변경
    val sampleRoutines = listOf(
        Routine(
            routineId = 1,
            title = "아침 운동",
            description = "상쾌한 아침을 여는 10분 스트레칭",
            imageUrl = null,
            category = "건강",
            tags = listOf("#모닝루틴", "#스트레칭"),
            authorId = 1,
            authorName = "사용자",
            authorProfileUrl = null,
            likes = 16,
            isLiked = true,
            isBookmarked = false,
            isRunning = false,
            scheduledTime = LocalTime.of(8, 0)
        ),
        Routine(
            routineId = 2,
            title = "오전 명상",
            description = "차분한 하루를 위한 명상 시간",
            imageUrl = null,
            category = "정신",
            tags = listOf("#마음챙김", "#집중"),
            authorId = 2,
            authorName = "사용자",
            authorProfileUrl = null,
            likes = 25,
            isLiked = false,
            isBookmarked = true,
            isRunning = true,
            scheduledTime = LocalTime.of(9, 30)
        )
    )
    MORUTheme {
        MyRoutinesScreen(
            uiState = MyRoutinesUiState(),
            routinesToDisplay = sampleRoutines,
            onSortOptionSelected = {},
            onDaySelected = {},
            onTrashClick = {},
            onCheckRoutine = { _, _ -> },
            onDeleteClick = {},
            onDismissDeleteDialog = {},
            onConfirmDelete = {},
            onOpenTimePicker = {},
            onCloseTimePicker = {},
            onConfirmTimeSet = { _, _, _, _ -> },
            onLikeClick = {},
            onShowInfoTooltip = {},
            onDismissInfoTooltip = {},
            onNavigateToCreateRoutine = {},
            onNavigateToRoutineFeed = {},
            onDismissDeleteSuccessDialog = {},
            onNavigateToDetail={}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "내 루틴 - 삭제 모드")
@Composable
private fun MyRoutinesScreenDeleteModePreview() {
    // [수정] Preview용 샘플 데이터를 새로운 Routine 클래스로 변경
    val sampleRoutines = listOf(
        Routine(
            routineId = 1,
            title = "아침 운동",
            description = "",
            imageUrl = null,
            category = "건강",
            tags = listOf("#모닝루틴", "#스트레칭"),
            authorId = 3,
            authorName = "사용자",
            authorProfileUrl = null,
            likes = 16,
            isLiked = true,
            isBookmarked = false,
            isRunning = false,
            isChecked = true
        ),
        Routine(
            routineId = 2,
            title = "오전 명상",
            description = "",
            imageUrl = null,
            category = "정신",
            tags = listOf("#마음챙김", "#집중"),
            authorId = 4,
            authorName = "사용자",
            authorProfileUrl = null,
            likes = 25,
            isLiked = false,
            isBookmarked = true,
            isRunning = true,
            isChecked = false
        )
    )
    MORUTheme {
        MyRoutinesScreen(
            uiState = MyRoutinesUiState(isDeleteMode = true),
            routinesToDisplay = sampleRoutines,
            onSortOptionSelected = {},
            onDaySelected = {},
            onTrashClick = {},
            onCheckRoutine = { _, _ -> },
            onDeleteClick = {},
            onDismissDeleteDialog = {},
            onConfirmDelete = {},
            onOpenTimePicker = {},
            onCloseTimePicker = {},
            onConfirmTimeSet = { _, _, _, _ -> },
            onLikeClick = {},
            onShowInfoTooltip = {},
            onDismissInfoTooltip = {},
            onNavigateToCreateRoutine = {},
            onNavigateToRoutineFeed = {},
            onDismissDeleteSuccessDialog = {},
            onNavigateToDetail = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "내 루틴 - 툴팁 표시")
@Composable
private fun MyRoutinesScreenWithTooltipPreview() {
    MORUTheme {
        MyRoutinesScreen(
            uiState = MyRoutinesUiState(showInfoTooltip = true),
            routinesToDisplay = emptyList(),
            onSortOptionSelected = {},
            onDaySelected = {},
            onTrashClick = {},
            onCheckRoutine = { _, _ -> },
            onDeleteClick = {},
            onDismissDeleteDialog = {},
            onConfirmDelete = {},
            onOpenTimePicker = {},
            onCloseTimePicker = {},
            onConfirmTimeSet = { _, _, _, _ -> },
            onLikeClick = {},
            onShowInfoTooltip = {},
            onDismissInfoTooltip = {},
            onNavigateToCreateRoutine = {},
            onNavigateToRoutineFeed = {},
            onDismissDeleteSuccessDialog = {},
            onNavigateToDetail = {}
        )
    }
}