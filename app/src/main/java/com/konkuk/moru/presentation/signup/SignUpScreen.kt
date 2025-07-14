package com.konkuk.moru.presentation.signup

import android.opengl.ETC1.isValid
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.presentation.signup.component.SignUpTextField
import com.konkuk.moru.presentation.signup.component.TopBarLogoWithTitle
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.konkuk.moru.presentation.signup.component.SignUpButton

@Composable
fun SignUpScreen(navController: NavController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isEmailValid by remember { mutableStateOf(false) }
    val isPasswordValid = remember(password) {
        password.length >= 8 && password.contains(Regex("[0-9]")) && password.contains(Regex("[!@#\$%^&*(),.?\":{}|<>]"))
    }
    val isFormValid = isEmailValid && isPasswordValid
    //val isFormValid = true // 임시로 유효성 검사 생략

    var emailInvalidMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.charcoalBlack)
            .systemBarsPadding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus() // 화면을 탭하면 포커스를 해제
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF1A1A1A)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TopBarLogoWithTitle()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFFFFFF))
                .padding(horizontal = 32.dp)
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(screenHeight * 0.05f))
                Text(
                    text = "회원가입",
                    style = typography.body_SB_24
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "이메일",
                    style = typography.body_SB_16
                )

                Spacer(modifier = Modifier.height(6.dp))

                SignUpTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        emailInvalidMessage =
                            if (email.isNotBlank() && !isEmailValid) {
                                "이메일 형식이 올바르지 않습니다."
                            } else {
                                null
                            }
                    },
                    isValid = email.isEmpty() || isEmailValid,
                    placeholder = "이메일",
                    keyboardType = KeyboardType.Email
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    emailInvalidMessage?.let {
                        Text(
                            text = it,
                            style = typography.desc_M_12,
                            color = colors.red,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }
                }

                Text(
                    text = "비밀번호",
                    style = typography.body_SB_16
                )

                Spacer(modifier = Modifier.height(6.dp))

                SignUpTextField(
                    value = password,
                    onValueChange = { password = it },
                    isValid = password.isEmpty() || isPasswordValid,
                    placeholder = "비밀번호",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "8자 이상, 숫자 특수문자를 포함해주세요.",
                    style = typography.desc_M_12,
                    color = if (password.isNotBlank() && !isPasswordValid) {
                        colors.red
                    } else {
                        colors.mediumGray
                    },
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            SignUpButton(
                enabled = isFormValid
            ) {
                if (isFormValid) {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    // 유효성 검사 실패 시 처리
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    SignUpScreen(navController = navController)
}