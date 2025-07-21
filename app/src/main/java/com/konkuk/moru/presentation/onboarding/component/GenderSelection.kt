package com.konkuk.moru.presentation.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun GenderSelection(
    selectedGender: String,
    onGenderSelect: (String) -> Unit
) {
    val genders = listOf("남자", "여자")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color = colors.veryLightGray, shape = RoundedCornerShape(6.dp))
    ) {
        genders.forEach { gender ->
            val isSelected = selectedGender == gender

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onGenderSelect(gender) } // 💡 클릭 이벤트 처리
                    .background(
                        color = if (isSelected) colors.paleLime else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) colors.limeGreen else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gender,
                    style = typography.body_SB_14,
                    color = if (isSelected) colors.oliveGreen else colors.mediumGray
                )
            }
        }
    }
}