package com.konkuk.moru.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.colors

@Composable
fun ProfileSettingCard(
    image: Int = R.drawable.ic_basic_profile,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Profile Image",
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)
                .background(color = colors.lightGray, shape = RoundedCornerShape(40.dp)),
            alignment = Alignment.Center
        )
        Image(
            painter = painterResource(id = R.drawable.ic_profile_edit),
            contentDescription = "Edit Profile",
            modifier = Modifier
                .padding(1.dp)
                .width(20.dp)
                .height(20.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Preview
@Composable
private fun ProfileSettingCardPreview() {
    ProfileSettingCard { }
}