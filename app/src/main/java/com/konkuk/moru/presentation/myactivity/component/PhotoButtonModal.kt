package com.konkuk.moru.presentation.myactivity.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun PhotoButtonModal(
    showImagePickerSheet: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(179.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(111.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clickable { showImagePickerSheet.value = false }
                ) {
                    Text(
                        text = "앨범에서 선택",
                        style = typography.desc_M_16,
                        color = colors.black
                    )
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.lightGray)) {}
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clickable { showImagePickerSheet.value = false }
                ) {
                    Text(
                        text = "사진 찍기",
                        style = typography.desc_M_16,
                        color = colors.black
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .clickable { showImagePickerSheet.value = false }
            ) {
                Text(
                    text = "취소",
                    style = typography.desc_M_16,
                    color = colors.black
                )
            }
        }
    }
}