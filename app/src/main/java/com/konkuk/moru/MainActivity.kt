package com.konkuk.moru

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.core.datastore.OnboardingPreference
import com.konkuk.moru.presentation.navigation.AppNavGraph
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.ui.theme.MORUTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import com.google.firebase.messaging.FirebaseMessaging

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val focusViewModel: RoutineFocusViewModel by viewModels()

    // --- FCM 알림 권한 요청 로직 추가 ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM_PERMISSION", "알림 권한이 허용되었습니다.")
        } else {
            Log.d("FCM_PERMISSION", "알림 권한이 거부되었습니다.")
        }
    }

    private fun askNotificationPermission() {
        // 이 코드는 Android 13 (TIRAMISU) 이상에서만 실행됩니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // 권한 요청
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // ------------------------------------

    // [추가] 앱 시작 시 onNewToken을 기다리지 말고 즉시 토큰을 로그로 찍어 확인
    private fun logFcmTokenNow() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "토큰 가져오기 실패", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_TOKEN", "Current token: $token")
        }
    }


    override fun onBackPressed() {
        if (focusViewModel.isFocusRoutineActive) {
            // 뒤로가기 버튼은 루틴 종료 알림창 팝업 표시
            focusViewModel.showScreenBlockPopup(focusViewModel.selectedApps)
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(
            "MainActivity",
            "🔍 onKeyDown 호출: keyCode=$keyCode, isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}"
        )

        if (focusViewModel.isFocusRoutineActive) {
            when (keyCode) {
                KeyEvent.KEYCODE_MENU -> {
                    Log.d("MainActivity", "📱 메뉴 버튼 감지 - 화면 차단 오버레이 표시")
                    focusViewModel.showScreenBlockOverlay(focusViewModel.selectedApps)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        super.onPause()
        Log.d(
            "MainActivity",
            "⏸️ onPause 호출: isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}"
        )
        if (focusViewModel.isFocusRoutineActive && !focusViewModel.isPermittedAppLaunch) {
            // 앱이 백그라운드로 갈 때는 기존 팝업 표시
            focusViewModel.showScreenBlockPopup(focusViewModel.selectedApps)
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d(
            "MainActivity",
            "🚪 onUserLeaveHint 호출: isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}"
        )
        if (focusViewModel.isFocusRoutineActive) {
            // 사용자가 홈 버튼이나 최근 앱 버튼을 눌렀을 때 화면 차단 오버레이 표시
            // 더 빠른 반응을 위해 즉시 실행
            focusViewModel.showScreenBlockOverlay(focusViewModel.selectedApps)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(
            "MainActivity",
            "🔍 onWindowFocusChanged: hasFocus=$hasFocus, isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}"
        )
        if (!hasFocus && focusViewModel.isFocusRoutineActive) {
            // 앱이 포커스를 잃었을 때도 오버레이 표시
            focusViewModel.showScreenBlockOverlay(focusViewModel.selectedApps)
        }
    }

    // ✅✅✅ 1. 오류 수정: Intent?를 Intent로 변경 ✅✅✅
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        logFcmTokenNow()

        setContent {
            MORUTheme {
                val navController = rememberNavController()
                val context = applicationContext
                val isLoggedInState = remember { mutableStateOf(false) }
                var isOnboardingComplete by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    isOnboardingComplete = OnboardingPreference
                        .isOnboardingComplete(applicationContext)
                        .first()
                    isLoggedInState.value = LoginPreference.isLoggedIn(context).first()
                }

                AppNavGraph(
                    navController = navController,
                    routineFocusViewModel = focusViewModel
                )

                // ✅✅✅ 2. 알림 클릭 시 전달된 데이터 처리 로직 추가 ✅✅✅
                // LaunchedEffect의 key를 intent로 설정하여 새 인텐트가 들어올 때마다 이 블록이 다시 실행되도록 합니다.
                LaunchedEffect(key1 = intent) {
                    val routineId = intent.getStringExtra("ROUTINE_ID")
                    if (!routineId.isNullOrEmpty()) {
                        Log.d("FCM_ROUTING", "알림에서 routineId($routineId)를 받아 화면 이동을 시도합니다.")
                        // TODO: "Route.RoutineDetail.createRoute(routineId)" 와 같이 실제 프로젝트의 네비게이션 경로에 맞게 수정해주세요.
                        navController.navigate("routineDetail/$routineId")

                        // ✅ 처리가 끝난 인텐트의 데이터를 지워 중복 실행을 방지합니다.
                        intent.removeExtra("ROUTINE_ID")
                    }
                }

                Log.d("FIREBASE", "google_app_id=" + getString(R.string.google_app_id))

                LaunchedEffect(focusViewModel.isLandscapeMode) {
                    Log.d(
                        "MainActivity",
                        "🔍 가로모드 상태 변경 감지: isLandscapeMode=${focusViewModel.isLandscapeMode}"
                    )
                    val newOrientation = if (focusViewModel.isLandscapeMode) {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    } else {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    if (requestedOrientation != newOrientation) {
                        Log.d(
                            "MainActivity",
                            "🔄 화면 방향 변경: ${if (focusViewModel.isLandscapeMode) "가로" else "세로"} 모드"
                        )
                        requestedOrientation = newOrientation
                    }
                }
            }
        }
    }
}