package com.konkuk.moru.presentation.routinefeed.component.topAppBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
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
 * @param onDaySelected 요일 탭 선택 시 동작 (선택된 요일의 인덱스를 전달, 아무것도 선택되지 않으면 null 전달)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineTopAppBar(
    onInfoClick: () -> Unit,
    onTrashClick: () -> Unit,
    onDaySelected: (Int?) -> Unit, // 변경점 1: 콜백 파라미터 타입을 Int?로 변경
    modifier: Modifier = Modifier
) {
    // 변경점 2: 상태 변수를 Int? 타입으로 변경하고 초기값을 null로 설정
    var selectedDayIndex by remember { mutableStateOf<Int?>(null) }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")


    Column(modifier = modifier.background(Color(0xFF212120))) {
        // 1. 상단 바 (변경 없음)
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
                containerColor = Color.Transparent,
                actionIconContentColor = Color.Gray
            )
        )

        // 2. 요일 선택 탭
        TabRow(
            selectedTabIndex = selectedDayIndex ?: -1, // 선택된 탭이 없을 경우 기본값으로 -1 전달
            containerColor = Color.Transparent,
            modifier = Modifier.graphicsLayer(scaleY = -1f),
            contentColor = Color.White,
            indicator = { tabPositions ->
                // 변경점 3: 선택된 인덱스가 null이 아닐 때만 인디케이터(밑줄)를 표시
                selectedDayIndex?.let {
                    if (it < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[it]),
                            color = Color.White
                        )
                    }
                }
            }
        ) {
            days.forEachIndexed { index, day ->
                Tab(
                    selected = selectedDayIndex == index,
                    onClick = {
                        // 변경점 4: 토글 로직 구현 및 콜백 호출
                        selectedDayIndex = if (selectedDayIndex == index) null else index
                        onDaySelected(selectedDayIndex)
                    },
                    text = {
                        Text(
                            modifier = Modifier
                                .graphicsLayer(scaleY = -1f)
                                .heightIn(min = 25.dp)
                                .widthIn(min = 80.dp),
                            text = day,
                            fontSize = 12.sp,
                            fontFamily = moruFontRegular,
                            maxLines = 1,          // ① 한 줄만
                            softWrap = false,
                            color = if (selectedDayIndex == index) Color.White else Color.Gray,
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