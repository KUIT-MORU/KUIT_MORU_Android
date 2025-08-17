package com.konkuk.moru.presentation.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.core.datastore.OnboardingPreference
import com.konkuk.moru.presentation.navigation.Route
import kotlinx.coroutines.flow.first

@Composable
fun AuthCheckScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("AuthCheckScreen", "🚀 AuthCheckScreen 시작!")
        
        try {
            // 앱 첫 설치 감지 및 강제 초기화
            val sharedPrefs = context.getSharedPreferences("app_install_check", android.content.Context.MODE_PRIVATE)
            val isFirstInstall = sharedPrefs.getBoolean("is_first_install", true)
            
            if (isFirstInstall) {
                Log.d("AuthCheckScreen", "🚨 앱 첫 설치 감지 - 강제 초기화 실행")
                try {
                    // 로그인 상태 강제 초기화
                    LoginPreference.setLoggedIn(context, false)
                    Log.d("AuthCheckScreen", "✅ 로그인 상태 초기화 완료")
                } catch (e: Exception) {
                    Log.e("AuthCheckScreen", "❌ 로그인 상태 초기화 실패", e)
                }
                
                // 첫 설치 플래그 제거
                sharedPrefs.edit().putBoolean("is_first_install", false).apply()
                Log.d("AuthCheckScreen", "✅ 첫 설치 플래그 제거 완료")
            }
            
            val isLoggedIn = LoginPreference.isLoggedIn(context).first()
            val isOnboarded = OnboardingPreference.isOnboardingComplete(context).first()

            Log.d("AuthCheckScreen", "🔍 인증 상태 확인:")
            Log.d("AuthCheckScreen", "   - isLoggedIn: $isLoggedIn")
            Log.d("AuthCheckScreen", "   - isOnboarded: $isOnboarded")

            val targetRoute = when {
                !isLoggedIn -> {
                    Log.d("AuthCheckScreen", "✅ 로그인 화면으로 이동")
                    Route.Login.route
                }
                !isOnboarded -> {
                    Log.d("AuthCheckScreen", "✅ 온보딩 화면으로 이동")
                    Route.Onboarding.route
                }
                else -> {
                    Log.d("AuthCheckScreen", "✅ 홈 화면으로 이동")
                    Route.Main.route
                }
            }

            Log.d("AuthCheckScreen", "🔄 네비게이션 시작: $targetRoute")
            navController.navigate(targetRoute) {
                popUpTo(0) { inclusive = true } // 백스택 전부 제거
            }
            Log.d("AuthCheckScreen", "✅ 네비게이션 완료")
        } catch (e: Exception) {
            Log.e("AuthCheckScreen", "❌ AuthCheckScreen 오류 발생", e)
            // 오류 발생 시 로그인 화면으로 이동
            Log.d("AuthCheckScreen", "🔄 오류로 인해 로그인 화면으로 이동")
            navController.navigate(Route.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}