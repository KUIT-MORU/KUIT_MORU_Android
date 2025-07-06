package com.konkuk.moru.presentation.routinefeed.component.TopAppBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme
import com.konkuk.moru.ui.theme.moruFontRegular
import com.konkuk.moru.ui.theme.moruFontSemiBold

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

    Column(modifier = modifier.background(Color(0xFF212120))) {
        // 1. 상단 바
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
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onTrashClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = "삭제",
                        modifier = Modifier.size(24.dp)
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
                            modifier=Modifier.height(15.dp).width(25.dp),
                            text = day,
                            fontSize = 12.sp,
                            fontFamily = moruFontRegular,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray,
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