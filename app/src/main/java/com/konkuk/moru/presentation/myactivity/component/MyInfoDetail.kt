package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun MyInfoDetail(
    myInfoDetailIcon: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(colors.lightGray)
                .height(1.dp)
        ) {  }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(57.dp)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier
            ){
                Icon(
                    painterResource(myInfoDetailIcon),
                    contentDescription = "MyInfoDetail Icon",
                    tint = colors.mediumGray,
                    modifier = Modifier
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, style = typography.body_SB_14)
            }
            Icon(
                painterResource(R.drawable.ic_arrow_b),
                contentDescription = "Arrow Icon",
                tint = colors.mediumGray,
                modifier = Modifier
                    .height(12.dp)
                    .width(5.dp)
            )
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(colors.lightGray)
                .height(1.dp)
        ) {  }
    }
}