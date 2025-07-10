package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.core.util.modifier.noRippleClickable
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import com.konkuk.moru.R
import com.konkuk.moru.presentation.navigation.Route

@Composable
fun MyActivityTab(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val tabTitles = listOf("내 정보", "인사이트")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 78.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(200.dp))
                .background(color = colors.veryLightGray)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize().padding(start=4.dp, end =4.dp)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(200.dp))
                            .background(color = if (index == selectedTab) Color(0xFFFFFFFF) else colors.veryLightGray)
                            .noRippleClickable {onTabSelected(index)},
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = typography.body_SB_14,
                            color = if (index == selectedTab)
                                colors.black
                            else
                                colors.mediumGray
                        )
                    }
                }
            }
        }

        when (selectedTab) {
            0 -> {
                Spacer(modifier = Modifier.height(24.dp))
                MyInfoDetail(myInfoDetailIcon = R.drawable.ic_heart_a , title = "내 관심 태그", modifier = Modifier.noRippleClickable { navController.navigate(
                    Route.ActFabTag.route) })
                Spacer(modifier = Modifier.height(6.dp))
                MyInfoDetail(myInfoDetailIcon = R.drawable.ic_graph_a , title = "내 기록", modifier = Modifier.noRippleClickable { navController.navigate(
                    Route.ActRecord.route) })
                Spacer(modifier = Modifier.height(6.dp))
                MyInfoDetail(myInfoDetailIcon = R.drawable.ic_scrap_a , title = "스크랩한 루틴", modifier = Modifier.noRippleClickable { navController.navigate(
                    Route.ActRecord.route) })
            }

            1 -> {
                InsightContentSection(70)
            }
        }
    }
}