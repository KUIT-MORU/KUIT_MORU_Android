package com.konkuk.moru.presentation.routinecreate.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
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
fun StepList(
    title: String? = null,
    time: String? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth().height(57.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = colors.mediumGray
        )
        Row(
            modifier = Modifier.weight(1f).padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_steplist_burger),
                contentDescription = "Step List Icon",
                modifier = Modifier.size(24.dp),
                tint = colors.lightGray
            )
            Spacer(modifier = Modifier.width(21.dp))
            Text(
                text = title?: "활동명",
                style = typography.body_SB_14,
                modifier = Modifier.weight(0.34f) // 전체의 약 34%
            )

            Text(
                text = time?: "소요 시간",
                style = typography.body_SB_14,
                modifier = Modifier.weight(0.32f)
            )// 전체의 약 32%
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = colors.mediumGray
        )
    }
}

@Preview
@Composable
private fun StepListPreview() {
    Column {
        Spacer( modifier = Modifier.height(20.dp))
        StepList()
        Spacer( modifier = Modifier.height(20.dp))
    }

}