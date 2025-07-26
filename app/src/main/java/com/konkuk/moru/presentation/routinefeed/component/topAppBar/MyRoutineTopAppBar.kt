package com.konkuk.moru.presentation.routinefeed.component.topAppBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontSemiBold
import java.time.DayOfWeek
import java.util.Locale

/**
 * '내 루틴' 화면의 TopAppBar와 요일 선택 탭
 * (상태 호이스팅이 적용되어 재사용 및 관리가 용이한 버전)
 *
 * @param onInfoClick 정보 아이콘 클릭 시 동작
 * @param onTrashClick 휴지통 아이콘 클릭 시 동작
 * @param onDaySelected 요일 탭 선택 시 동작 (선택된 DayOfWeek 전달, 해제 시 null 전달)
 * @param selectedDay 외부에서 전달받는 현재 선택된 요일 (State)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineTopAppBar(
    onInfoClick: () -> Unit,
    onTrashClick: () -> Unit,
    onDaySelected: (DayOfWeek?) -> Unit,
    selectedDay: DayOfWeek?,
    modifier: Modifier = Modifier
) {
    val days = DayOfWeek.values() // MONDAY ~ SUNDAY

    Column(modifier = modifier.background(Color(0xFF212120))) {
        // 1. 상단 바 (기존 UI 스타일 유지)
        TopAppBar(
            title = {
                Text(
                    text = "내 루틴",
                    color = MORUTheme.colors.limeGreen,
                    fontFamily = moruFontSemiBold,
                    fontWeight = FontWeight(600)
                )
            },
            actions = {
                IconButton(onClick = onInfoClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "정보",
                        modifier = Modifier.size(24.dp) // 기존 스타일
                    )
                }
                IconButton(onClick = onTrashClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = "삭제",
                        modifier = Modifier.size(24.dp) // 기존 스타일
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                actionIconContentColor = Color.Gray
            )
        )

        // 2. 요일 선택 탭 (기존 UI 스타일 + 새로운 기능 로직)
        TabRow(
            selectedTabIndex = selectedDay?.value?.minus(1) ?: -1,
            modifier = Modifier.graphicsLayer(scaleY = -1f),
            containerColor = Color.Transparent,
            contentColor = Color.White,
            indicator = { tabPositions ->
                selectedDay?.let {
                    val index = it.value - 1
                    if (index in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = Color.White
                        )
                    }
                }
            }
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                Tab(
                    modifier = Modifier
                        //.width(80.dp).height(32.dp), // 기존 스타일
                        .weight(1f)
                        .height(32.dp),
                    selected = isSelected,
                    onClick = {
                        // 기능: 같은 요일 클릭 시 선택 해제(null), 다른 요일 클릭 시 선택
                        val newSelection = if (isSelected) null else day
                        onDaySelected(newSelection)
                    },
                    text = {
                        Text(
                            // "MONDAY" -> "Mon" 형태로 변환하여 기존 스타일과 일치
                            text = day.name.take(3).lowercase()
                                .replaceFirstChar { it.titlecase(Locale.getDefault()) },
                            style = MORUTheme.typography.time_R_12,
                            maxLines = 1,
                            softWrap = false,
                            // 기존 스타일: 선택 여부에 따라 색상 변경
                            color = if (isSelected) Color.White else Color.Gray,
                            modifier = Modifier
                                .graphicsLayer(scaleY = -1f)
                                .heightIn(min = 25.dp) // 기존 스타일
                        )
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MyRoutineTopAppBarPreview() {
    // 위 '어떻게 사용하나요?' 에서 설명한 방식의 실제 사용 예시입니다.
    var selectedDay by remember { mutableStateOf<DayOfWeek?>(DayOfWeek.MONDAY) }

    MaterialTheme {
        MyRoutineTopAppBar(
            onInfoClick = {},
            onTrashClick = {},
            selectedDay = selectedDay,
            onDaySelected = { day ->
                selectedDay = day
            }
        )
    }
}