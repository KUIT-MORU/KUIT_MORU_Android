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
            // 앱 첫 설치 및 버전 변경 감지
            val sharedPrefs = context.getSharedPreferences("app_install_check", android.content.Context.MODE_PRIVATE)
            val isFirstInstall = sharedPrefs.getBoolean("is_first_install", true)
            val currentVersion = "1.0.0" // 앱 버전 (실제로는 BuildConfig에서 가져와야 함)
            val savedVersion = sharedPrefs.getString("app_version", null)
            val isVersionChanged = savedVersion != currentVersion
            
            // 개발용 강제 초기화 플래그 (필요시 true로 설정)
            // 앱을 삭제해도 DataStore가 남아있을 때 이 값을 true로 설정하면 강제 초기화됨
            val forceReset = false
            
            if (isFirstInstall || isVersionChanged || forceReset) {
                Log.d("AuthCheckScreen", "🚨 초기화 조건 감지:")
                Log.d("AuthCheckScreen", "   - isFirstInstall: $isFirstInstall")
                Log.d("AuthCheckScreen", "   - isVersionChanged: $isVersionChanged (saved: $savedVersion, current: $currentVersion)")
                Log.d("AuthCheckScreen", "   - forceReset: $forceReset")
                
                try {
                    // DataStore 완전 초기화
                    LoginPreference.clearAllData(context)
                    Log.d("AuthCheckScreen", "✅ DataStore 완전 초기화 완료")
                } catch (e: Exception) {
                    Log.e("AuthCheckScreen", "❌ DataStore 초기화 실패", e)
                }
                
                // 설치/버전 정보 업데이트
                sharedPrefs.edit()
                    .putBoolean("is_first_install", false)
                    .putString("app_version", currentVersion)
                    .apply()
                Log.d("AuthCheckScreen", "✅ 설치/버전 정보 업데이트 완료")
            }
            
            val isLoggedIn = LoginPreference.isLoggedIn(context).first()
            val isOnboarded = OnboardingPreference.isOnboardingComplete(context).first()

            Log.d("AuthCheckScreen", "🔍 인증 상태 확인: isLoggedIn=$isLoggedIn, isOnboarded=$isOnboarded")
            if (isLoggedIn && !isOnboarded) {
                Log.w("AuthCheckScreen", "⚠️ 로그인은 되었으나 OnboardingPreference=false 입니다. 로그인 시 server isOnboarding 반영 여부를 확인하세요.")
            }

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