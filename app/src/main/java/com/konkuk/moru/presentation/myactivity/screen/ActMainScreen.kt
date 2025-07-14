package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.presentation.myactivity.component.ActMyInfo
import com.konkuk.moru.presentation.myactivity.component.MyActivityTab
import com.konkuk.moru.presentation.myactivity.component.MyProfileTitle
import com.konkuk.moru.presentation.myactivity.component.RoutinePaceCard
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun ActMainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ){
        MyProfileTitle(navController)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 16.dp, end = 16.dp)
                .background(colors.lightGray)
        ){}

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            ActMyInfo(4, 628, 221, "정해찬", "루틴페이스 메이커", 0.7f, navController = navController)

            var selectedTab by remember { mutableStateOf(0) }
            MyActivityTab(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}