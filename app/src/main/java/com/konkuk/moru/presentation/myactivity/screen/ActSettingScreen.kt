package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.presentation.myactivity.component.BackTitle
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun ActSettingScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.veryLightGray)
            .padding(start = 16.dp, end = 16.dp)
    ){
        Spacer(modifier = Modifier.padding(16.dp))
        BackTitle(title = "설정", navController)
        Spacer(modifier = Modifier.padding(48.dp))
        Column(){

        }
    }
}