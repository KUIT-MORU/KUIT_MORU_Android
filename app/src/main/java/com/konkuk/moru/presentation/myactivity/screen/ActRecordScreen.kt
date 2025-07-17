package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.presentation.myactivity.component.BackTitle
import com.konkuk.moru.presentation.myactivity.component.RecordCard
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun ActRecordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val dummyData = listOf(
        Triple("루틴 이름 1", listOf("공부", "운동"), false),
        Triple("루틴 이름 2", listOf("자기계발", "아침루틴"), true),
        Triple("루틴 이름 3", listOf("영어", "책읽기"), true),
        Triple("루틴 이름 4", listOf("일기쓰기", "스트레칭"), false),
        Triple("루틴 이름 5", listOf("명상", "감사일기"), true),
        Triple("루틴 이름 6", listOf("저녁루틴", "복습"), false),
        Triple("루틴 이름 7", listOf("알고리즘", "CS공부"), true),
        Triple("루틴 이름 8", listOf("걷기", "산책"), false),
        Triple("루틴 이름 9", listOf("플러터", "Compose"), true),
        Triple("루틴 이름 10", listOf("뉴스보기", "시사공부"), false),
        Triple("루틴 이름 6", listOf("저녁루틴", "복습"), false),
        Triple("루틴 이름 7", listOf("알고리즘", "CS공부"), true),
        Triple("루틴 이름 8", listOf("걷기", "산책"), false),
        Triple("루틴 이름 9", listOf("플러터", "Compose"), true),
        Triple("루틴 이름 10", listOf("뉴스보기", "시사공부"), false)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        BackTitle(title = "내 기록", navController = navController)
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 90.dp),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues()
        ) {
            items(dummyData) { (title, tags, isComplete) ->
                RecordCard(
                    title = title,
                    tags = tags,
                    completeFlag = isComplete,
                    time = "00:00:00",
                    onClick = {
                        navController.navigate(Route.ActRecordDetail.createRoute(title))
                    }
                )
            }
        }
    }
}