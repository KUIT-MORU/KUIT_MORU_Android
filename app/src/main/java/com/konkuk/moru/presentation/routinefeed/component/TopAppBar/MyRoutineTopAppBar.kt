package com.konkuk.moru.presentation.routinefeed.component.TopAppBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.moru.R

/**
 * '내 루틴' 화면에서 사용하는 TopAppBar와 요일 선택 탭
 * @param onInfoClick 정보 아이콘 클릭 시 동작
 * @param onTrashClick 휴지통 아이콘 클릭 시 동작
 * @param onDaySelected 요일 탭 선택 시 동작 (선택된 요일의 인덱스를 전달)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineTopAppBar(
    onInfoClick: () -> Unit,
    onTrashClick: () -> Unit,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val Lime = Color(0xFFB7C400) // '내 루틴' 텍스트 색상

    Column(modifier = modifier.background(Color.DarkGray)) {
        // 1. 상단 바
        TopAppBar(
            title = {
                Text(
                    text = "내 루틴",
                    color = Lime,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = onInfoClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "정보"
                    )
                }
                IconButton(onClick = onTrashClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = "삭제"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent, // Column 배경색을 사용하기 위해 투명으로
                actionIconContentColor = Color.Gray
            )
        )

        // 2. 요일 선택 탭
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color.White // 선택된 탭 하단 선 색상
                    )
                }
            }
        ) {
            days.forEachIndexed { index, day ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        onDaySelected(index)
                    },
                    text = {
                        Text(
                            text = day,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray
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
    MaterialTheme {
        MyRoutineTopAppBar(
            onInfoClick = {},
            onTrashClick = {},
            onDaySelected = {}
        )
    }
}