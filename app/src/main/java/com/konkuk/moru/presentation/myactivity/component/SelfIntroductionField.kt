package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun SelfIntroductionField(modifier: Modifier = Modifier) {
    var introText by remember { mutableStateOf("") }
    val maxLength = 20

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "자기소개",
            style = typography.body_SB_16,
            color = colors.black,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFD9D9D9), RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(6.dp))
        ) {
            BasicTextField(
                value = introText,
                onValueChange = {
                    if (it.length <= maxLength) {
                        introText = it
                    }
                },
                textStyle = typography.desc_M_14.copy(color = colors.black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                decorationBox = { innerTextField ->
                    if (introText.isEmpty()) {
                        Text(
                            text = "자기소개",
                            style = typography.desc_M_14,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}