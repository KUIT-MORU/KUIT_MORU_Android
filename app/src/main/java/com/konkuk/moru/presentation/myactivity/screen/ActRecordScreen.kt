package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.konkuk.moru.R
import com.konkuk.moru.presentation.myactivity.component.RecordCard
import com.konkuk.moru.presentation.myactivity.viewmodel.MyActRecordUi
import com.konkuk.moru.presentation.myactivity.viewmodel.MyActRecordViewModel
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import com.konkuk.moru.ui.theme.MORUTheme.typography
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

@Composable
fun ActRecordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: MyActRecordViewModel = hiltViewModel()
) {
    val todayList by vm.today.collectAsState()
    val recentList by vm.recent.collectAsState()
    val allList by vm.all.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadToday()
        vm.loadRecent()
        vm.loadAllFirst()
    }

    val today = remember { LocalDate.now() }
    val last7List by remember(allList, today) {
        derivedStateOf {
            allList.filter { (today.toEpochDay() - it.startedAt.toEpochDay()) in 1..6 }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // BackTitle 대신 커스텀 뒤로가기 버튼 사용
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_a),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        // 내 활동 화면으로 이동
                        navController.navigate(Route.MyActivity.route) {
                            // 현재 화면(내 기록)을 백스택에서 제거
                            popUpTo(Route.ActRecord.route) { inclusive = true }
                            // 내 활동 화면으로 이동
                            launchSingleTop = true
                        }
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "내 기록", style = typography.body_SB_16)
        }
        Spacer(modifier = Modifier.height(16.dp))

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
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(
                items = todayList,
                key = { rec: MyActRecordUi -> "today-${rec.id}" }
            ) { rec ->
                val safe = URLEncoder.encode(rec.title, StandardCharsets.UTF_8.toString())
                RecordCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(90f / 139f),
                    title = rec.title,
                    tags = rec.tags,
                    completeFlag = rec.isComplete,
                    time = rec.durationSec.toHms(),
                    onClick = { navController.navigate(Route.ActRecordDetail.createRoute(rec.id)) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "최근 7일",
                    color = colors.black,
                    style = typography.body_SB_14,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(items = recentList, key = { rec: MyActRecordUi -> "recent-${rec.id}" }) { rec ->
                val safe = URLEncoder.encode(rec.title, StandardCharsets.UTF_8.toString())
                RecordCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(90f / 139f),
                    title = rec.title,
                    tags = rec.tags,
                    completeFlag = rec.isComplete,
                    time = rec.durationSec.toHms(),
                    onClick = { navController.navigate(Route.ActRecordDetail.createRoute(rec.id)) }
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "전체",
                    color = colors.black,
                    style = typography.body_SB_14,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            itemsIndexed(allList, key = { idx, rec -> "all-${rec.id}-$idx" }) { index, rec ->
                if (index >= allList.lastIndex - 4) {
                    vm.loadAllNext()
                }
                val safe = URLEncoder.encode(rec.title, StandardCharsets.UTF_8.toString())
                RecordCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(90f / 139f),
                    title = rec.title,
                    tags = rec.tags,
                    completeFlag = rec.isComplete,
                    time = rec.durationSec.toHms(),
                    onClick = { navController.navigate(Route.ActRecordDetail.createRoute(rec.id)) }
                )
            }
        }
    }
}

private fun Long.toHms(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
