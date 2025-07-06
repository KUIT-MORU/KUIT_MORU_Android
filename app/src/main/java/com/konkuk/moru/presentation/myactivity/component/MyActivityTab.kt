package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.moru.core.util.modifier.noRippleClickable
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun MyActivityTab(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabTitles = listOf("내 정보", "인사이트")

    Column {
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

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("내 정보 구현 예정")
                }
            }

            1 -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("인사이트 구현 예정")
                }
            }
        }
    }
}