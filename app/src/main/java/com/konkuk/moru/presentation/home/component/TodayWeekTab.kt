package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun TodayWeekTab(
    modifier: Modifier,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabTitles = listOf("오늘", "이번주")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = colors.black,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = colors.black
            )
        }
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = typography.desc_M_14,
                        color = if (selectedTabIndex == index) colors.black else colors.mediumGray
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun TodayWeekTabPreview() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    TodayWeekTab(
        modifier = Modifier,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { selectedTabIndex = it }
    )
}