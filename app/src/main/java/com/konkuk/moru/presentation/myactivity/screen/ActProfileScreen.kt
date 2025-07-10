package com.konkuk.moru.presentation.myactivity.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.konkuk.moru.R
import com.konkuk.moru.core.util.modifier.noRippleClickable
import com.konkuk.moru.presentation.myactivity.component.BackTitle
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography

@Composable
fun ActProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val isEditMode = remember { mutableStateOf(false) }

    val profileData = remember {
        mutableStateListOf(
            mutableStateOf("모루유저"),
            mutableStateOf("여자"),
            mutableStateOf("2025.06.28"),
            mutableStateOf("안녕하세요!")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
            .padding(top = 14.dp, start = 16.dp, end = 16.dp)
    ) {
        BackTitle(title = "내 프로필", navController)

        Spacer(modifier = Modifier.height(38.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.CenterHorizontally)
                .background(color = colors.veryLightGray, shape = CircleShape)
        ){
            Image(
                painter = painterResource(id = R.drawable.ic_profile_basic),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(55.69.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(68.dp)
                .height(32.dp)
                .align(Alignment.CenterHorizontally)
                .background(if (isEditMode.value) colors.paleLime else colors.veryLightGray, shape = RoundedCornerShape(30.dp)),
        ){
            Text(
                text = if (isEditMode.value) "완료" else "수정",
                color = if (isEditMode.value) colors.oliveGreen else colors.black,
                style = typography.desc_M_16,
                modifier = Modifier
                    .noRippleClickable { isEditMode.value = !isEditMode.value }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 닉네임
        Text("닉네임")
        if (isEditMode.value) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = profileData[0].value,
                    onValueChange = { profileData[0].value = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "중복확인",
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.Gray
                )
            }
        } else {
            OutlinedText(profileData[0].value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 성별
        Text("성별", style = MaterialTheme.typography.bodyMedium)
        if (isEditMode.value) {
            Row {
                listOf("남자", "여자").forEach { gender ->
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (profileData[1].value == gender) Color(0xFFB8EE44) else Color(0xFFF1F3F5)
                            )
                            .clickable { profileData[1].value = gender }
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(gender)
                    }
                }
            }
        } else {
            OutlinedText(profileData[1].value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 생년월일
        Text("생년월일", style = MaterialTheme.typography.bodyMedium)
        if (isEditMode.value) {
            TextField(
                value = profileData[2].value,
                onValueChange = { profileData[2].value = it },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedText(profileData[2].value)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 자기소개
        Text("자기소개")
        if (isEditMode.value) {
            TextField(
                value = profileData[3].value,
                onValueChange = { profileData[3].value = it },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedText(profileData[3].value)
        }
    }
}

@Composable
fun OutlinedText(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Text(text)
    }
}

