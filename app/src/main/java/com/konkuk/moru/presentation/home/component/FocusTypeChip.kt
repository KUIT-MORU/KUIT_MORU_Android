package com.konkuk.moru.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.presentation.home.FocusType
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun FocusTypeChip(
    focusType: FocusType,
    modifier: Modifier = Modifier
) {
    val text = when (focusType) {
        FocusType.FOCUS -> "집중"
        FocusType.SIMPLE -> "간편"
    }

    Box(
        modifier = modifier
            .background(
                color = colors.paleLime,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 9.5.dp, vertical = 3.5.dp)
    ) {
        Text(
            text = text,
            style = typography.body_SB_16,
            color = colors.oliveGreen
        )
    }
}


@Preview
@Composable
private fun FocusTypeChipPreview() {
    Column {
        FocusTypeChip(FocusType.FOCUS)
        FocusTypeChip(FocusType.SIMPLE)
    }
}