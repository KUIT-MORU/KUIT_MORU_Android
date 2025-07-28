package com.konkuk.moru.presentation.onboarding.page

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.core.component.ProfileSettingCard
import com.konkuk.moru.core.component.TopBarLogoWithTitle
import com.konkuk.moru.presentation.onboarding.component.BirthdayField
import com.konkuk.moru.presentation.onboarding.component.GenderSelection
import com.konkuk.moru.presentation.onboarding.component.IntroductionField
import com.konkuk.moru.presentation.onboarding.component.NickNameTextField
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.moru.core.component.button.MoruButtonTypeA
import com.konkuk.moru.presentation.onboarding.OnboardingViewModel
import com.konkuk.moru.presentation.onboarding.component.ImageOptionButtonScreen
import java.util.Calendar

@Composable
fun UserInfoPage(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel? = null
    //viewModel: OnboardingViewModel = hiltViewModel() //Todo: 프리뷰용으로 대체
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var nickName by remember { mutableStateOf("") }
    var isNickNameValid by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("") }
    var birthDay by remember { mutableStateOf("") }
    var introduction by remember { mutableStateOf("") }

    val isFormValid = isNickNameValid && gender.isNotBlank() && birthDay.isNotBlank()
    var isImageOptionVisible by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = "%04d.%02d.%02d".format(year, month + 1, dayOfMonth)
                birthDay = selectedDate
                viewModel?.updateBirthday(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.charcoalBlack)
            .systemBarsPadding()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            TopBarLogoWithTitle()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFFFFFFF))
                    .padding(bottom = 10.dp, top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "안녕하세요! 사용자님을 소개해주세요",
                        style = typography.body_SB_20,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "언제든 수정 가능해요!",
                        style = typography.desc_M_14,
                        color = colors.mediumGray,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileSettingCard() {
                        focusManager.clearFocus()
                        isImageOptionVisible = true // 메뉴 띄우기
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "닉네임",
                            style = typography.body_SB_16
                        )
                        if (isNickNameValid) {
                            Text(
                                text = "사용 가능한 닉네임입니다.",
                                style = typography.desc_M_12,
                                color = colors.limeGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    NickNameTextField(
                        value = nickName,
                        onValueChange = {
                            nickName = it
                            isNickNameValid = it.length in 2..10
                            if (isNickNameValid) viewModel?.updateNickname(it)
                        },
                        isValid = isNickNameValid,
                        placeholder = "닉네임",
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "성별",
                        style = typography.body_SB_16,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GenderSelection(
                        selectedGender = gender,
                        onGenderSelect = {
                            gender = it
                            viewModel?.updateGender(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "생년월일",
                        style = typography.body_SB_16,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BirthdayField(birthday = birthDay, onClick = { datePickerDialog.show() })
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "자기소개",
                        style = typography.body_SB_16,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    IntroductionField(
                        value = introduction,
                        onValueChange = {
                            introduction = it
                            viewModel?.updateIntroduction(it)
                        }
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_onboarding_statusbar1),
                        contentDescription = "status bar",
                        modifier = Modifier.width(134.dp)
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    MoruButtonTypeA(text = "다음", enabled = isFormValid) {onNext()}
                }
            }
        }
        if (isImageOptionVisible){
            ImageOptionButtonScreen(
                onImageSelected = {
                    //Todo 앨범에서 이미지 선택 로직 구현
                },
                onCameraSelected = {
                    //Todo 카메라로 사진 찍는 로직 구현
                },
                onCancel = { isImageOptionVisible = false }
            )
        }
    }
}

@Preview
@Composable
private fun UserInfoPagePreview() {
    UserInfoPage(onNext = {})
}