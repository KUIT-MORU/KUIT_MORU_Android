package com.konkuk.moru.core.component.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun MoruButtonTypeA(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (enabled) colors.charcoalBlack else colors.mediumGray
    val textColor = if (enabled) colors.paleLime else Color.White
    Button(
        //onClick = { if (enabled) onClick() },
        onClick = onClick, // Todo 임시로 항상 작동하도록 설정함
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Text(
            text = text,
            style = typography.body_SB_16
        )
    }
}

@Preview
@Composable
private fun SignUpButtonPreview() {
    MoruButtonTypeA(text = "회원가입", enabled = true) {}
}