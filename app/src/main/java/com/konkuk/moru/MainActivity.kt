package com.konkuk.moru

import android.net.http.SslCertificate.restoreState
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.presentation.home.screen.HomeScreen
import com.konkuk.moru.presentation.navigation.BottomNavItem
import com.konkuk.moru.presentation.navigation.MainNavGraph
import com.konkuk.moru.presentation.navigation.Route
import com.konkuk.moru.ui.theme.MORUTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MORUTheme {
                HomeScreen()
//                val navController = rememberNavController()
//                val navBackStackEntry by navController.currentBackStackEntryAsState()
//                val currentRoute = navBackStackEntry?.destination?.route
//
//
//                val bottomNavItems = listOf(
//                    BottomNavItem(Route.Home.route, R.drawable.ic_home),
//                    BottomNavItem(Route.RoutineFeed.route, R.drawable.ic_routine_feed),
//                    BottomNavItem(Route.MyRoutine.route, R.drawable.ic_my_routine),
//                    BottomNavItem(Route.MyActivity.route, R.drawable.ic_my_activity)
//                )
//
//                Scaffold(
//                    modifier = Modifier
//                        .systemBarsPadding(),
//                    contentWindowInsets = WindowInsets.safeDrawing,
//                    bottomBar = {
//                        NavigationBar(
//                            modifier = Modifier
//                                .drawBehind {
//                                    val strokeWidth = 1.dp.toPx()
//                                    drawLine(
//                                        color = Color(0x4D000000),
//                                        start = Offset(0f, 0f),
//                                        end = Offset(size.width, 0f),
//                                        strokeWidth = strokeWidth,
//                                    )
//                                }
//                                .height(41.dp),
//                            containerColor = Color.White,
//                        ) {
//                            bottomNavItems.forEach { item ->
//                                NavigationBarItem(
//                                    selected = currentRoute == item.route,
//                                    onClick = {
//                                        if (currentRoute != item.route) {
//                                            navController.navigate(item.route) {
//                                                launchSingleTop = true
//                                                restoreState = true
//                                            }
//                                        }
//                                    },
//                                    icon = {
//                                        Icon(
//                                            modifier = Modifier.size(18.dp),
//                                            painter = painterResource(id = item.icon),
//                                            tint = Color.Black,
//                                            contentDescription = "bottomBarIcon",
//                                        )
//                                    }
//
//                                )
//                            }
//
//                        }
//                    }
//                ) { innerPadding ->
//                    MainNavGraph(
//                        navController = navController,
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
            }
        }
    }
}