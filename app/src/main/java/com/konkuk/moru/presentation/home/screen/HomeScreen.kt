package com.konkuk.moru.presentation.home.screen

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Text(text = "Home Screen", modifier = modifier.background(color = colors.black50Opacity), style = typography.head_EB_24)
}