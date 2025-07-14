package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.core.util.modifier.softShadow
import com.konkuk.moru.ui.theme.MORUTheme.colors
import kotlinx.coroutines.launch

@Composable
fun InsightGraph() {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(231.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                modifier = Modifier
                    .weight(1f)
                    .softShadow(
                        color = Color.Black,
                        alpha = 0.12f,
                        shadowRadius = 16.dp,
                        cornerRadius = 16.dp
                    )
                    .background(Color.White, shape = RoundedCornerShape(4.dp))
            ) { page ->
                when (page) {
                    0 -> InsightGraphA(70f, 80f)
                    1 -> InsightGraphB(
                        userName = "정해찬",
                        weekdayUser = 0.4f,
                        weekdayAll = 0.7f,
                        weekendUser = 0.5f,
                        weekendAll = 1.0f
                    )
                    2 -> InsightGraphC(
                        morning = 60f,
                        afternoon = 45f,
                        night = 100f,
                        lateNight = 25f
                    )

                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            PageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = 3
            )
        }
    }

}

@Composable
fun GraphPage3() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.lightGray)
    ) {
        Text(text = "그래프 3")
    }
}