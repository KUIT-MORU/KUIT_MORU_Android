package com.konkuk.moru.presentation.routinefeed.component.modale


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit = {},
    showTwoButtons: Boolean,
    dialogColor: Color = Color(0xFF2C2C2C),
    content: @Composable () -> Unit,
    onConfirmationcontent: String = "확인",
    onDismisscontent: String = "취소"
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            // 1. 다이얼로그 크기 고정
            modifier = Modifier.size(width = 312.dp, height = 164.dp),
            shape = RoundedCornerShape(4.dp), // 사진과 유사하게 모서리 변경
            color = dialogColor
        ) {
            // 2. Column 레이아웃 재구성
            Column(
                modifier = Modifier.fillMaxSize(), // Surface를 꽉 채우도록 변경
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 메인 컨텐츠를 담는 Box, 위쪽 여백 추가
                Box(
                    modifier = Modifier.padding(top = 61.dp),
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }

                // 남는 공간을 모두 차지해서 아래 요소들을 밀어내는 Spacer
                Spacer(modifier = Modifier.weight(1f))

                // 버튼 표시 여부에 따라 UI 분기
                if (showTwoButtons) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp), // 버튼 영역의 높이 지정
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 왼쪽 절반 영역 (취소 버튼)
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(onClick = { onDismissRequest() }) {
                                Text(
                                    onDismisscontent,
                                    color = Color(0xFFEBFFC0),
                                    fontSize = 14.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                        // 오른쪽 절반 영역 (확인 버튼)
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(onClick = { onConfirmation() }) {
                                Text(
                                    onConfirmationcontent,
                                    color = Color(0xFFEBFFC0),
                                    fontSize = 14.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Preview 코드도 dialogColor를 맞춰서 확인하기 용이하도록 수정
@Preview(name = "Custom Dialog Preview")
@Composable
fun CustomDialogCustomTextPreview() {
    CustomDialog(
        showTwoButtons = true,
        dialogColor = Color(0xFF212120), // 사진과 유사한 배경색으로 변경
        onConfirmation = {},
        onDismissRequest = {},
        content = {
            Text(text = "수정을 완료하시겠습니까?", color = Color(0xFFFFFFFF)) // 사진과 유사한 글자색으로 변경
        },
        onDismisscontent = "취소",
        onConfirmationcontent = "확인"
    )
}