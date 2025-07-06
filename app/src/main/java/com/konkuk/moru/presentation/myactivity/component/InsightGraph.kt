package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import kotlinx.coroutines.launch

@Composable
fun InsightGraph() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(231.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (pagerState.currentPage > 0) {
            Box(
                modifier = Modifier
                    .width(7.5.dp)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_d),
                    contentDescription = "다음",
                    modifier = Modifier
                        .width(7.5.dp)
                        .height(15.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(7.5.dp))
        }

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) { page ->
            when (page) {
                0 -> GraphPage1()
                1 -> GraphPage2()
                2 -> GraphPage3()
            }
        }

        if (pagerState.currentPage < 2) {
            Box(
                modifier = Modifier
                    .width(7.5.dp)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_c),
                    contentDescription = "다음",
                    modifier = Modifier
                        .width(7.5.dp)
                        .height(15.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(7.5.dp))
        }
    }
}

@Composable
fun GraphPage1() {
    Box(modifier = Modifier.fillMaxSize().background(colors.lightGray)) {
        Text(text = "그래프 1")
    }
}

@Composable
fun GraphPage2() {
    Box(modifier = Modifier.fillMaxSize().background(colors.lightGray)) {
        Text(text = "그래프 2")
    }
}

@Composable
fun GraphPage3() {
    Box(modifier = Modifier.fillMaxSize().background(colors.lightGray)) {
        Text(text = "그래프 3")
    }
}