package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.konkuk.moru.ui.theme.MORUTheme.colors

// 1. 데이터 클래스
data class RoutineData(
    val title: String,        // 제목
    val hashtag: String,      // 해시태그
    val heartCount: Int,      // 하트 수
    val day: String,          // 요일
    val time: String          // 시간
)

// 2. Pager 컴포저블
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TodayRoutinePager(
    modifier: Modifier = Modifier,
    routines: List<RoutineData>
) {
    //현재 페이지가 몇 페이지인지 기억하기
    val pagerState = rememberPagerState(initialPage = 0)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            count = routines.size,
            state = pagerState,
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp)
        ) { page ->
            val routine = routines[page]
            TodayRoutineListBoxItem(
                title = routine.title,
                hashtag = routine.hashtag,
                heartCount = routine.heartCount,
                day = routine.day,
                time = routine.time
            )
        }

        Spacer(modifier = Modifier.height(19.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = modifier
                .padding(4.dp),
            activeColor = colors.black,
            inactiveColor = colors.lightGray
        )
    }

}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun TodayRoutinePagerPreview() {
    val sampleRoutines = listOf(
        RoutineData("주말 아침 루틴", "#화이팅", 25, "토일", "am 09:00 ~ am 09:58"),
        RoutineData("출근 준비 루틴", "#힘내자", 41, "월", "am 08:00 ~ am 08:45"),
        RoutineData("운동 루틴", "#건강", 12, "수", "pm 06:00 ~ pm 07:00")
    )

    TodayRoutinePager(routines = sampleRoutines)
}