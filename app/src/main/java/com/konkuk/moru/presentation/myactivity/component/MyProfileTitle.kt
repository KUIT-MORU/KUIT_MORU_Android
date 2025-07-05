package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun MyProfileTitle(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = "MY PROFILE",
            style = typography.body_SB_16,
            color = colors.black,
            modifier = Modifier
        )

        Icon(
            painterResource(R.drawable.ic_gear),
            contentDescription = "Settings Icon",
            tint = colors.mediumGray,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(20.dp)
        )
    }
}

@Preview
@Composable
private fun MyProfileTitlePreview() {
    MyProfileTitle()
}