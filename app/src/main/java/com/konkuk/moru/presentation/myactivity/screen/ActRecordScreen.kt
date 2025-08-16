package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.presentation.myactivity.component.BackTitle
import com.konkuk.moru.presentation.myactivity.component.RecordCard
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

data class RecordUi(
    val id: String,
    val title: String,
    val tags: List<String>,
    val isComplete: Boolean,
    val startedAt: LocalDate
)

@Composable
fun ActRecordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val all = remember {
        listOf(
            RecordUi("1","루틴 이름 1", listOf("공부","운동"), false, today),
            RecordUi("2","루틴 이름 2", listOf("자기계발","아침루틴"), true,  today),
            RecordUi("3","루틴 이름 3", listOf("영어","책읽기"), true,  today.minusDays(1)),
            RecordUi("4","루틴 이름 4", listOf("일기쓰기","스트레칭"), false, today.minusDays(2)),
            RecordUi("5","루틴 이름 5", listOf("명상","감사일기"), true,  today.minusDays(6)),
            RecordUi("6","루틴 이름 6", listOf("저녁루틴","복습"), false, today.minusDays(7)),
            RecordUi("7","루틴 이름 7", listOf("알고리즘","CS공부"), true,  today.minusDays(10))
        )
    }

    val todayList by remember(all, today) {
        derivedStateOf { all.filter { it.startedAt == today } }
    }
    val last7List by remember(all, today) {
        derivedStateOf {
            all.filter { (today.toEpochDay() - it.startedAt.toEpochDay()) in 1..6 }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        BackTitle(title = "내 기록", navController = navController)
        Spacer(Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 90.dp),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "오늘",
                    color = colors.black,
                    style = typography.body_SB_14,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            items(
                items = todayList,
                key = { rec -> "today-${rec.id}" }
            ) { rec ->
                val safe = URLEncoder.encode(rec.title, StandardCharsets.UTF_8.toString())
                RecordCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(90f / 139f),
                    title = rec.title,
                    tags = rec.tags,
                    completeFlag = rec.isComplete,
                    time = "00:00:00",
                    onClick = { navController.navigate(Route.ActRecordDetail.createRoute(safe)) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "최근 7일",
                    color = colors.black,
                    style = typography.body_SB_14,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            items(
                items = last7List,
                key = { rec -> "last7-${rec.id}" }
            ) { rec ->
                val safe = URLEncoder.encode(rec.title, StandardCharsets.UTF_8.toString())
                RecordCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(90f / 139f),
                    title = rec.title,
                    tags = rec.tags,
                    completeFlag = rec.isComplete,
                    time = "00:00:00",
                    onClick = { navController.navigate(Route.ActRecordDetail.createRoute(safe)) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "전체",
                    color = colors.black,
                    style = typography.body_SB_14,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            items(
                items = all,
                key = { rec -> "all-${rec.id}" }
            ) { rec ->
                val safe = URLEncoder.encode(rec.title, StandardCharsets.UTF_8.toString())
                RecordCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(90f / 139f),
                    title = rec.title,
                    tags = rec.tags,
                    completeFlag = rec.isComplete,
                    time = "00:00:00",
                    onClick = { navController.navigate(Route.ActRecordDetail.createRoute(safe)) }
                )
            }
        }
    }
}
