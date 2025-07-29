package com.konkuk.moru.core.component

import android.R.attr.label
import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun MoruBottomBar(
    modifier: Modifier = Modifier,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    onIconMeasured: (Int, String, Offset) -> Unit // 홈화면 온보딩에 아이콘 위치에 유동적으로 맞추기 위해 추가함
) {
    Log.d("StepCheck", "✅ MoruBottomBar 호출됨")

    val items = listOf(
        BottomNavItem(
            title = "홈",
            route = Route.Home.route,
            iconResId = R.drawable.ic_home,
            selectedIconResId = R.drawable.ic_home
        ),
        BottomNavItem(
            title = "루틴 피드",
            route = Route.RoutineFeed.route,
            iconResId = R.drawable.ic_routine_feed,
            selectedIconResId = R.drawable.ic_routine_feed
        ),
        BottomNavItem(
            title = "내 루틴",
            route = Route.MyRoutine.route,
            iconResId = R.drawable.ic_my_routine,
            selectedIconResId = R.drawable.ic_my_routine
        ),
        BottomNavItem(
            title = "내 활동",
            route = Route.MyActivity.route,
            iconResId = R.drawable.ic_my_activity,
            selectedIconResId = R.drawable.ic_my_activity
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {
        items.forEachIndexed { idx,item ->
            val isSelected = selectedRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item.route) },
                // 위치 측정 코드 추가
                modifier = Modifier.onGloballyPositioned { c ->
                    val pos   = c.positionInRoot()          // 좌상단(px)
                    val size  = c.size                      // IntSize(px)
                    val center = Offset(
                        pos.x + size.width  / 2f,
                        pos.y + size.height / 2f
                    )

                    Log.d("StepCheck", "🟢 아이콘 측정됨: [$idx] ${item.title} center=$center")

                    onIconMeasured(idx, item.title, center) // → 부모로 보고
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (isSelected) item.selectedIconResId else item.iconResId
                        ),
                        contentDescription = item.title,
                        modifier = Modifier
                            .width(16.dp)
                            .height(17.5.dp),
                        tint = if (isSelected) colors.black else colors.lightGray
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) colors.black else colors.lightGray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = colors.black,
                    unselectedIconColor = colors.lightGray,
                    selectedTextColor = colors.black,
                    unselectedTextColor = colors.lightGray
                )
            )
        }
    }
}

@Preview
@Composable
private fun MoruBottomBarPreview() {
    MoruBottomBar(
        selectedRoute = Route.Home.route,
        onItemSelected = {},
        onIconMeasured = { _, _, _ -> } // 더미 함수로 채움
    )
}