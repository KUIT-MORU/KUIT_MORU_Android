package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun BackTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(24.dp)
    ){
        Icon(
            painter = painterResource(R.drawable.ic_arrow_a),
            contentDescription = "Back Icon",
            modifier = Modifier
                .size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = typography.body_SB_16)
    }
}

@Preview
@Composable
private fun BackTitlePreview() {
    BackTitle("스크랩한 루틴")
}