package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RoutineCardList(modifier: Modifier = Modifier) {
    //스크롤 대비 상태 저장
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        //나중에 진짜 루틴들 받아올 것
        repeat(5) {
            RoutineCardItem()
        }
    }
}

@Preview
@Composable
private fun RoutineCardListPreview() {
    RoutineCardList()
}