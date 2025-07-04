package com.konkuk.moru.presentation.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.home.component.HomeTopAppBar
import com.konkuk.moru.presentation.home.component.RoutineCardList
import com.konkuk.moru.presentation.home.component.RoutineData
import com.konkuk.moru.presentation.home.component.TodayRoutinePager
import com.konkuk.moru.presentation.home.component.TodayWeekTab
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    //탭 선택 상태(오늘,이번주)
    var selectedTab by remember { mutableStateOf(0) }

    // 루틴 샘플 데이터
    val sampleRoutines = listOf(
        RoutineData("주말 아침 루틴", "#화이팅", "토일 am 09:00 ~ am 09:58", 0.25f),
        RoutineData("출근 준비 루틴", "#힘내자", "월 am 08:00 ~ am 08:45", 0.6f),
        RoutineData("운동 루틴", "#건강", "수 pm 06:00 ~ pm 07:00", 0.9f)
    )
    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HomeTopAppBar()
            Spacer(modifier = modifier.size(26.dp))

            Column() {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 360.dp, height = 111.dp)
                ) {
                    // 1.인삿말
                    Text(
                        text = "XX님,\n오늘은 어떤 루틴을 시작할까요?",
                        style = typography.title_B_20,

                        )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = colors.lightGray,
                    thickness = 1.dp
                )
                Column(
                    modifier = modifier
                        .padding(16.dp)
                ) {
                    // 2. Today
                    Text(
                        text = "TODAY",
                        style = typography.desc_M_16,
                        color = colors.black,
                    )
                    // 3. 월 일 요일
                    Text(
                        text = "5월 10일 토",
                        style = typography.head_EB_24,
                        color = colors.black
                    )
                    Text(
                        text = "정기 루틴이 있는 날이에요",
                        style = typography.desc_M_16,
                        color = colors.black
                    )
                }

                // 4. 탭 선택
                TodayWeekTab(
                    selectedTabIndex = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                // 선택된 탭에 따라 콘텐츠 분기
                Spacer(modifier = modifier.size(10.dp))
                when (selectedTab) {
                    0 -> TodayRoutinePager(routines = sampleRoutines)
                    //1 ->//TODO:이번주 탭 선택 시 호출할 함수
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = colors.lightGray,
                    thickness = 7.dp
                )
                Spacer(modifier = modifier.size(3.dp))
                //루틴 목록
                Text(
                    text = "루틴 목록 >",
                    style = typography.desc_M_16,
                    color = colors.black,
                    modifier = modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = modifier.size(8.dp))
                RoutineCardList()
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}