package com.konkuk.moru.presentation.routinefeed.component.modale

import android.os.Build
import androidx.annotation.RequiresApi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.button.MoruButton
import com.konkuk.moru.ui.theme.MORUTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Locale
import kotlin.math.abs

enum class RepeatMode { NONE, EVERYDAY, WEEKDAYS, WEEKENDS }

/**
 * 메인 스크린. 하단 시트를 제어합니다.
 * @param isPreview 프리뷰 환경인지 여부. true일 경우 시트가 초기에 열린 상태로 보입니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeSet(isPreview: Boolean = false) {
    // Material3에 맞는 올바른 state 선언
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    // 프리뷰 여부에 따라 시트 노출을 결정하는 상태
    var showSheet by remember { mutableStateOf(isPreview) }

    // 메인 스크린의 배경 UI (예시)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            // 버튼 클릭 시 상태를 true로 변경하여 시트를 보여줌
            showSheet = true
        }) {
            Text("시간 설정 열기")
        }
    }

    // showSheet 상태가 true일 때만 ModalBottomSheet를 띄움
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                // 닫힐 때 상태를 false로 변경
                showSheet = false
            },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = Color.White
        ) {
            TimePickerSheetContent(
                onConfirm = { time, days, alarm ->
                    println("설정된 시간: $time, 요일: $days, 알림: $alarm")
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showSheet = false
                        }
                    }
                }
            )
        }
    }
}


/**
 * 하단 시트 내부에 들어갈 컨텐츠
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
 fun TimePickerSheetContent(  //private 삭제함 일단
    onConfirm: (LocalTime, Set<DayOfWeek>, Boolean) -> Unit
) {
    var selectedAmPm by remember { mutableStateOf("오후") }
    var selectedHour by remember { mutableStateOf(2) }
    var selectedMinute by remember { mutableStateOf(1) }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var repeatMode by remember { mutableStateOf(RepeatMode.NONE) }
    var isAlarmOn by remember { mutableStateOf(true) }

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

    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "반복",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        val itemHeight = 40.dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * 3),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WheelPicker(
                    items = listOf("오전", "오후"),
                    itemHeight = itemHeight,
                    initialItem = selectedAmPm,
                    onSelectionChanged = { selectedAmPm = it }
                )
                WheelPicker(
                    items = (1..12).map { it.toString().padStart(2, '0') },
                    itemHeight = itemHeight,
                    initialItem = selectedHour.toString().padStart(2, '0'),
                    onSelectionChanged = { selectedHour = it.toInt() }
                )
                Text(
                    ":",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                WheelPicker(
                    items = (0..59).map { it.toString().padStart(2, '0') },
                    itemHeight = itemHeight,
                    initialItem = selectedMinute.toString().padStart(2, '0'),
                    onSelectionChanged = { selectedMinute = it.toInt() }
                )
            }
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
                .clickable { isAlarmOn = !isAlarmOn },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (isAlarmOn) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank
            val tint = if (isAlarmOn) Color.Black else Color.Gray

            Icon(imageVector = icon, contentDescription = "알림 받기", tint = tint)
            Spacer(modifier = Modifier.width(8.dp))
            Text("알림 받기", color = Color.Black, fontWeight = FontWeight(400))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoruButton(
                text = "초기화",
                onClick = { /* TODO: 초기화 로직 */ },
                backgroundColor = MORUTheme.colors.lightGray,
                contentColor = Color(0xFF595959),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(52.dp),
                iconContent = {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = R.drawable.ic_reset),
                        contentDescription = "초기화 아이콘"
                    )
                }
            )
            MoruButton(
                text = "확인",
                onClick = {
                    val hour24 = when {
                        selectedAmPm == "오후" && selectedHour != 12 -> selectedHour + 12
                        selectedAmPm == "오전" && selectedHour == 12 -> 0
                        else -> selectedHour
                    }
                    onConfirm(LocalTime.of(hour24, selectedMinute), selectedDays, isAlarmOn)
                },
                backgroundColor = MORUTheme.colors.limeGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
        }
    }
}

@Composable
private fun WheelPicker(
    items: List<String>,
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    initialItem: String,
    onSelectionChanged: (String) -> Unit
) {
    val itemCount = Int.MAX_VALUE
    val actualItemCount = items.size
    val centralIndex = itemCount / 2
    val initialIndex = centralIndex + (items.indexOf(initialItem) - (centralIndex % actualItemCount))

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior: FlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val derivedCentralItemIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            if (layoutInfo.visibleItemsInfo.isEmpty()) {
                0
            } else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                layoutInfo.visibleItemsInfo
                    .minByOrNull { abs(it.offset + it.size / 2 - viewportCenter) }
                    ?.index ?: 0
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { derivedCentralItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                onSelectionChanged(items[index % actualItemCount])
            }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier
            .height(itemHeight * 3)
            .width(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = itemHeight)
    ) {
        items(count = itemCount) { index ->
            val isSelected = index == derivedCentralItemIndex
            val scale = if (isSelected) 1.2f else 0.8f
            val alpha = if (isSelected) 1.0f else 0.4f

            Box(
                modifier = Modifier.height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items[index % actualItemCount],
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                )
            }
        }
    }
}

@Composable
private fun RepeatModeSelector(selectedMode: RepeatMode, onModeSelected: (RepeatMode) -> Unit) {
    val modes = mapOf("매일" to RepeatMode.EVERYDAY, "평일만" to RepeatMode.WEEKDAYS, "주말만" to RepeatMode.WEEKENDS)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        modes.forEach { (text, mode) ->
            val isSelected = selectedMode == mode
            Text(
                text = text,
                color = if (isSelected) Color.Black else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
                    .background(if (isSelected) MORUTheme.colors.limeGreen else Color.Transparent)
                    .clickable { onDayClick(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayShortName,
                    color = if (isSelected) Color.White else Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun RoutineScreenPreview() {
    // 프리뷰에서 바로 시트가 보이도록 isPreview = true 전달
    TimeSet(isPreview = true)
}