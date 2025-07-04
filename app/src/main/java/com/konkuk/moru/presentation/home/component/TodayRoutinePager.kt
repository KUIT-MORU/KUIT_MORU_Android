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
    val title: String,
    val hashtag: String,
    val dayAndTime: String,
    val progress: Float
)

// 2. Pager 컴포저블
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TodayRoutinePager(
    modifier: Modifier = Modifier,
    routines:List<RoutineData>
) {
    //현재 페이지가 몇 페이지인지 기억하기
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        HorizontalPager(
            count = routines.size,
            state = pagerState,
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp)
        ) { page->
            val routine = routines[page]
            TodayRoutineListBoxItem(
                title = routine.title,
                hashtag = routine.hashtag,
                dayAndTime = routine.dayAndTime,
                progress = routine.progress
            )
        }
        Spacer(modifier=modifier.size(8.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = modifier
                .padding(4.dp),
            activeColor = colors.black,
            inactiveColor = colors.lightGray
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun TodayRoutinePagerPreview() {
    val sampleRoutines = listOf(
        RoutineData("주말 아침 루틴", "#화이팅", "토일 am 09:00 ~ am 09:58", 0.25f),
        RoutineData("출근 준비 루틴", "#힘내자", "월 am 08:00 ~ am 08:45", 0.6f),
        RoutineData("운동 루틴", "#건강", "수 pm 06:00 ~ pm 07:00", 0.9f)
    )

    TodayRoutinePager(routines = sampleRoutines)
}