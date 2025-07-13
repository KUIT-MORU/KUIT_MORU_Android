package com.konkuk.moru.presentation.myactivity.component

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import java.util.Calendar

@Composable
fun MyBirthInputField(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var birthDate by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "생년월일",
            color = colors.black,
            style = typography.body_SB_16,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.lightGray, RoundedCornerShape(10.5.dp))
                .clip(RoundedCornerShape(10.5.dp))
                .height(45.dp)
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val formatted = String.format("%04d.%02d.%02d", year, month + 1, dayOfMonth)
                            birthDate = formatted
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (birthDate.isNotEmpty()) birthDate else "YYYY.MM.DD",
                    style = typography.desc_M_14,
                    color = if (birthDate.isNotEmpty()) colors.black else colors.mediumGray,
                    modifier = Modifier
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "달력",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}