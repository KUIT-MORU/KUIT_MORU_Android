package com.konkuk.moru.presentation.routinefeed.component.modale

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.konkuk.moru.ui.theme.MORUTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Locale
import kotlin.math.abs


// 데이터 관리를 위한 enum
enum class RepeatMode { NONE, EVERYDAY, WEEKDAYS, WEEKENDS }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSettingModal(
    onDismissRequest: () -> Unit,
    onConfirm: (LocalTime, Set<DayOfWeek>, Boolean) -> Unit
) {
    // 1. 상태 관리
    var selectedAmPm by remember { mutableStateOf("오전") }
    var selectedHour by remember { mutableStateOf(2) }
    var selectedMinute by remember { mutableStateOf(1) }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var repeatMode by remember { mutableStateOf(RepeatMode.NONE) }
    var isAlarmOn by remember { mutableStateOf(true) }

    // 2. 파생 상태 로직 (요일 선택)
    LaunchedEffect(repeatMode) {
        selectedDays = when (repeatMode) {
            RepeatMode.EVERYDAY -> DayOfWeek.values().toSet()
            RepeatMode.WEEKDAYS -> setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
            RepeatMode.WEEKENDS -> setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            RepeatMode.NONE -> setOf()
        }
    }
    val onDayClick = { day: DayOfWeek ->
        repeatMode = RepeatMode.NONE
        selectedDays = if (selectedDays.contains(day)) selectedDays - day else selectedDays + day
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(390.dp), // 높이 살짝 늘림
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF2C2C2C)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(),) {

                    Text(
                        "시간대 설정",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // --- 직접 구현한 시간 선택 피커 ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    WheelPicker(
                        items = listOf("오전", "오후"),
                        initialItem = selectedAmPm,
                        onSelectionChanged = { selectedAmPm = it }
                    )
                    WheelPicker(
                        items = (1..12).map { it.toString() },
                        initialItem = selectedHour.toString(),
                        onSelectionChanged = { selectedHour = it.toInt() }
                    )
                    Text(":", color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    WheelPicker(
                        items = (0..59).map { it.toString().padStart(2, '0') },
                        initialItem = selectedMinute.toString().padStart(2, '0'),
                        onSelectionChanged = { selectedMinute = it.toInt() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                RepeatModeSelector(selectedMode = repeatMode, onModeSelected = { repeatMode = it })
                Spacer(modifier = Modifier.height(8.dp))
                DayOfWeekSelector(selectedDays = selectedDays, onDayClick = onDayClick)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                        // Row 자체를 클릭 가능하게 만들어 터치 영역을 넓힙니다.
                        .clickable { isAlarmOn = !isAlarmOn },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // isAlarmOn 상태에 따라 아이콘을 선택합니다.
                    val icon = if (isAlarmOn) {
                        Icons.Outlined.CheckBox // ✅ 체크된 아이콘 (테두리만 있음)
                    } else {
                        Icons.Outlined.CheckBoxOutlineBlank // ⚪️ 체크 안 된 아이콘
                    }

                    // isAlarmOn 상태에 따라 아이콘 색상을 선택합니다.
                    val tint = if (isAlarmOn) {
                        Color.White  // 체크 시 색상
                    } else {
                        Color.White // 체크 안 됐을 때 색상
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = "알림 받기",
                        tint = tint
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격
                    Text("알림 받기", color = Color.White, fontWeight = FontWeight(400))
                }

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest, modifier = Modifier.weight(1f)) {
                        Text("닫기", color = Color(0xFFEBFFC0), fontSize = 16.sp)
                    }
                    TextButton(
                        onClick = {
                            val hour24 = when {
                                selectedAmPm == "오후" && selectedHour != 12 -> selectedHour + 12
                                selectedAmPm == "오전" && selectedHour == 12 -> 0
                                else -> selectedHour
                            }
                            onConfirm(LocalTime.of(hour24, selectedMinute), selectedDays, isAlarmOn)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("확인", color = Color(0xFFEBFFC0), fontSize = 14.sp, lineHeight = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun WheelPicker(
    items: List<String>,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 40.dp,
    initialItem: String,
    onSelectionChanged: (String) -> Unit
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (items.indexOf(initialItem) - 1).coerceAtLeast(0)
    )
    val flingBehavior: FlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // ✅ 중앙 인덱스 계산 로직을 더 안정적인 방식으로 수정
    val centralItemIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                -1
            } else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                visibleItemsInfo.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: -1
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                if (!isScrolling && centralItemIndex < items.size) {
                    onSelectionChanged(items[centralItemIndex])
                }
            }
    }

    Box(modifier = modifier.height(itemHeight * 3).width(80.dp), contentAlignment = Alignment.Center) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight)
        ) {
            items(count = items.size) { index ->
                val item = items[index]
                val distanceFromCenter = abs(index - centralItemIndex)
                val scale = 1.0f - (distanceFromCenter * 0.2f).coerceAtMost(0.4f)
                val alpha = 1.0f - (distanceFromCenter * 0.3f).coerceAtMost(0.7f)

                Box(
                    modifier = Modifier.height(itemHeight).padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = if (distanceFromCenter == 0) FontWeight.Bold else FontWeight.Normal
                        ),
                        modifier = Modifier.scale(scale).alpha(alpha)
                    )
                }
            }
        }
        // 중앙 선택 라인
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
        )
    }
}


// RepeatModeSelector와 DayOfWeekSelector는 이전과 동일하게 유지됩니다.
@Composable
private fun RepeatModeSelector(selectedMode: RepeatMode, onModeSelected: (RepeatMode) -> Unit) {
    val modes = mapOf("매일" to RepeatMode.EVERYDAY, "평일만" to RepeatMode.WEEKDAYS, "주말만" to RepeatMode.WEEKENDS)
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        modes.forEach { (text, mode) ->
            val isSelected = selectedMode == mode
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = if (isSelected) FontWeight(400) else FontWeight.Normal,
                modifier = Modifier.clickable { onModeSelected(mode) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayOfWeekSelector(selectedDays: Set<DayOfWeek>, onDayClick: (DayOfWeek) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        DayOfWeek.values().forEach { day ->
            val dayShortName = day.getDisplayName(java.time.format.TextStyle.SHORT, Locale.KOREAN)
            val isSelected = selectedDays.contains(day)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MORUTheme.colors.mediumGray else Color.Transparent)
                    .clickable { onDayClick(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayShortName,
                    color = if (isSelected) Color.White else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun TimeSettingModalPreview() {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        TimeSettingModal(
            onDismissRequest = { showDialog = false },
            onConfirm = { time, days, alarm ->
                println("설정된 시간: $time, 요일: $days, 알림: $alarm")
                showDialog = false
            }
        )
    }
}