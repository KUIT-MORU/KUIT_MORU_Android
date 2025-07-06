package com.konkuk.moru.presentation.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.LocalMoruColorsProvider
import com.konkuk.moru.ui.theme.LocalMoruTypographyProvider
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val colors = LocalMoruColorsProvider.current
    val texts = LocalMoruTypographyProvider.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "모루의\n기능을 알아볼까요?",
                style = texts.title_B_24,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onNextClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray, //버튼 배경색
                    contentColor = Color.White //텍스트 색
                )
            ) {
                Text(
                    text = "다음으로 >",
                    style = texts.desc_M_16,
                )
            }
        }
        //X버튼
        Text(
            text = "✕",
            color = Color.White,
            style = typography.body_SB_16,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .clickable { onCloseClick() }
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFEFEFEF
)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(
        onNextClick = {},
        onCloseClick = {}
    )
}