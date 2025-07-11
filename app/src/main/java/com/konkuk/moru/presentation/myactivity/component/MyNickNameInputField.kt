package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun MyNickNameInputField(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("none") }

    val borderColor = when (status) {
        "none" -> colors.limeGreen
        "focus" -> colors.limeGreen
        "valid" -> colors.limeGreen
        "error" -> colors.red
        else -> colors.lightGray
    }

    val statusText = when (status) {
        "valid" -> "사용 가능한 닉네임입니다."
        "error" -> "이미 사용 중인 닉네임입니다."
        else -> ""
    }

    val statusTextColor = when (status) {
        "valid" -> colors.limeGreen
        "error" -> colors.red
        else -> Color.Transparent
    }

    val buttonColor = when (status) {
        "none" -> colors.veryLightGray
        "valid" -> colors.paleLime
        "error" -> colors.red
        else -> colors.veryLightGray
    }

    val buttonTextColor = when (status) {
        "none" -> colors.mediumGray
        "valid" -> colors.oliveGreen
        "error" -> Color(0xFFFFFFFF)
        else -> colors.mediumGray

    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                "닉네임",
                color = colors.black,
                style = typography.body_SB_16,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            if (status != "none" && statusText.isNotEmpty()) {
                Text(statusText, color = statusTextColor, style = typography.desc_M_14)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                .padding(end = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        status = if (it.isEmpty()) "none" else "focus"
                    },
                    singleLine = true,
                    textStyle = typography.desc_M_14.copy(color = colors.black),
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (text.isEmpty()) {
                                Text(
                                    text = "닉네임",
                                    style = typography.desc_M_14,
                                    color = colors.mediumGray
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(56.dp)
                        .height(21.dp)
                        .clip(RoundedCornerShape(10.5.dp))
                        .background(buttonColor)
                ) {
                    Text(
                        text = "중복확인",
                        color = buttonTextColor,
                        style = typography.desc_M_12,
                        modifier = Modifier
                            .clickable {
                                status = when {
                                    text.isEmpty() -> "none"
                                    text == "error_text" -> "error"
                                    else -> "valid"
                                }
                            }
                    )
                }
            }
        }
    }
}
