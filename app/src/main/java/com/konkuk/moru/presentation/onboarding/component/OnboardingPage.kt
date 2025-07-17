package com.konkuk.moru.presentation.onboarding.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingPage(page: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when (page) {
            0 -> UserInfoInputPage()
            1 -> InfoPage("모루는 루틴을 쉽게 만들고\n기록할 수 있는 앱이에요.")
            2 -> TagSelectionPage()
            3 -> InfoPage("당신의 습관은 데이터를 통해\n더 의미 있어질 수 있어요.")
            4 -> PermissionRequestPage()
            5 -> InfoPage("이제 곧 당신만의 루틴 여정이\n시작됩니다.")
            6 -> FinalPage()
        }
    }
}

@Composable
fun InfoPage(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun UserInfoInputPage() {
    // TODO: 이름, 나이 등 사용자 정보 입력 UI
    Text("사용자 정보를 입력해주세요", fontSize = 20.sp)
}

@Composable
fun TagSelectionPage() {
    // TODO: 관심 태그 선택 UI
    Text("관심 태그를 선택해주세요", fontSize = 20.sp)
}

@Composable
fun PermissionRequestPage() {
    // TODO: 위치, 알림 등 권한 요청 안내
    Text("앱을 원활히 사용하기 위해\n권한을 허용해주세요", fontSize = 20.sp, textAlign = TextAlign.Center)
}

@Composable
fun FinalPage() {
    Text(
        text = "모두 설정 완료!\n이제 시작해볼까요?",
        fontSize = 22.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}