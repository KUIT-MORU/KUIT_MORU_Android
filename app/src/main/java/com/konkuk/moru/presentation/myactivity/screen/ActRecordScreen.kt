package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.presentation.myactivity.component.BackTitle
import com.konkuk.moru.presentation.myactivity.component.RecordCard
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun ActRecordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFFFFFFF))
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.padding(16.dp))
        BackTitle(title = "내 기록", navController = navController)
        Spacer(modifier = Modifier.padding(16.dp))

        RecordCard(
            title = "루틴 이름",
            tags = listOf("태그명", "태그명", "아주아주 긴 루틴 이름"),
            time = "00:00:00",
            completeFlag = true,
        )
    }
}