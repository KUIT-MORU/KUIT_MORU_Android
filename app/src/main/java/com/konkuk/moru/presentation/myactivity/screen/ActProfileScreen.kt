package com.konkuk.moru.presentation.myactivity.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.konkuk.moru.R
import com.konkuk.moru.core.util.modifier.noRippleClickable
import com.konkuk.moru.presentation.myactivity.component.BackTitle
import com.konkuk.moru.presentation.myactivity.component.MyBirthInputField
import com.konkuk.moru.presentation.myactivity.component.MyGenderInputField
import com.konkuk.moru.presentation.myactivity.component.MyNickNameInputField
import com.konkuk.moru.presentation.myactivity.component.PhotoButtonModal
import com.konkuk.moru.presentation.myactivity.component.SelfIntroductionField
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import kotlinx.coroutines.delay

@Composable
fun ActProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val isEditMode = rememberSaveable { mutableStateOf(false) }
    val showToast = rememberSaveable { mutableStateOf(false) }
    val showImagePickerSheet = rememberSaveable { mutableStateOf(false) }

    val profileData = remember {
        mutableStateListOf(
            mutableStateOf("모루유저"),
            mutableStateOf("여자"),
            mutableStateOf("2025.06.28"),
            mutableStateOf("안녕하세요!")
        )
    }

    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri.value = it }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri.value != null) {
            selectedImageUri.value = cameraUri.value
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(top = 14.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            BackTitle(title = "내 프로필", navController)
            Spacer(modifier = Modifier.height(38.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colors.veryLightGray, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri.value != null) {
                        AsyncImage(
                            model = selectedImageUri.value.toString(),
                            contentDescription = "Selected Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            onSuccess = {
                                Log.d("AsyncImage", "이미지 로드 성공: ${selectedImageUri.value}")
                            },
                            onError = {
                                Log.e(
                                    "AsyncImage",
                                    "이미지 로드 실패: ${selectedImageUri.value}",
                                    it.result.throwable
                                )
                            }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_basic),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(55.69.dp)
                        )
                    }
                }

                if (isEditMode.value) {
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .offset(x = -4.dp, y = -4.dp)
                            .align(Alignment.BottomEnd)
                            .clickable { showImagePickerSheet.value = true }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_edit_profile),
                            contentDescription = "Edit",
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(68.dp)
                    .height(32.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        if (isEditMode.value) colors.paleLime else colors.veryLightGray,
                        shape = RoundedCornerShape(30.dp)
                    )
            ) {
                Text(
                    text = if (isEditMode.value) "완료" else "수정",
                    color = if (isEditMode.value) colors.oliveGreen else colors.black,
                    style = typography.desc_M_16,
                    modifier = Modifier.noRippleClickable {
                        if (isEditMode.value) showToast.value = true
                        isEditMode.value = !isEditMode.value
                    }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
            if (isEditMode.value) MyNickNameInputField() else OutlinedText("닉네임", profileData[0].value)
            Spacer(modifier = Modifier.height(21.dp))
            if (isEditMode.value) MyGenderInputField() else OutlinedText("성별", profileData[1].value)
            Spacer(modifier = Modifier.height(21.dp))
            if (isEditMode.value) MyBirthInputField() else OutlinedText("생년월일", profileData[2].value)
            Spacer(modifier = Modifier.height(21.dp))
            if (isEditMode.value) SelfIntroductionField() else OutlinedText("자기소개", profileData[3].value)
        }
    }

    AnimatedVisibility(
        visible = showToast.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(colors.black, shape = RoundedCornerShape(10.dp))
                    .height(80.dp)
            ) {
                Text(
                    text = "수정되었습니다",
                    color = Color.White,
                    style = typography.desc_M_14
                )
            }
        }
    }

    LaunchedEffect(showToast.value) {
        if (showToast.value) {
            delay(1000L)
            showToast.value = false
        }
    }

    if (showImagePickerSheet.value) {
        PhotoButtonModal(
            showImagePickerSheet = showImagePickerSheet,
            galleryLauncher = galleryLauncher,
            cameraLauncher = cameraLauncher,
            cameraUri = cameraUri
        )
    }
}

@Composable
fun OutlinedText(title: String, text: String) {
    Text(
        text = title,
        color = colors.black,
        style = typography.body_SB_16,
        modifier = Modifier.padding(bottom = 6.dp)
    )
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .border(1.dp, colors.lightGray, RoundedCornerShape(10.5.dp))
            .padding(start = 16.dp)
    ) {
        Text(
            text = text,
            style = typography.desc_M_14,
            color = colors.mediumGray
        )
    }
}
