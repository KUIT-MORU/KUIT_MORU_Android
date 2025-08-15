package com.konkuk.moru

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.konkuk.moru.core.datastore.LoginPreference
import com.konkuk.moru.core.datastore.OnboardingPreference
import com.konkuk.moru.presentation.navigation.AppNavGraph
import com.konkuk.moru.presentation.routinefocus.viewmodel.RoutineFocusViewModel
import com.konkuk.moru.ui.theme.MORUTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val focusViewModel: RoutineFocusViewModel by viewModels()
    
    override fun onBackPressed() {
        if (focusViewModel.isFocusRoutineActive) {
            // 뒤로가기 버튼은 루틴 종료 알림창 팝업 표시
            focusViewModel.showScreenBlockPopup(focusViewModel.selectedApps)
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d("MainActivity", "🔍 onKeyDown 호출: keyCode=$keyCode, isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}")
        
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
        Log.d("MainActivity", "⏸️ onPause 호출: isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}")
        if (focusViewModel.isFocusRoutineActive && !focusViewModel.isPermittedAppLaunch) {
            // 앱이 백그라운드로 갈 때는 기존 팝업 표시
            focusViewModel.showScreenBlockPopup(focusViewModel.selectedApps)
        }
    }
    
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d("MainActivity", "🚪 onUserLeaveHint 호출: isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}")
        if (focusViewModel.isFocusRoutineActive) {
            // 사용자가 홈 버튼이나 최근 앱 버튼을 눌렀을 때 화면 차단 오버레이 표시
            // 더 빠른 반응을 위해 즉시 실행
            focusViewModel.showScreenBlockOverlay(focusViewModel.selectedApps)
        }
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d("MainActivity", "🔍 onWindowFocusChanged: hasFocus=$hasFocus, isFocusRoutineActive=${focusViewModel.isFocusRoutineActive}")
        if (!hasFocus && focusViewModel.isFocusRoutineActive) {
            // 앱이 포커스를 잃었을 때도 오버레이 표시
            focusViewModel.showScreenBlockOverlay(focusViewModel.selectedApps)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MORUTheme {
                val navController = rememberNavController()
                //val scope = rememberCoroutineScope()

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
            }
        }
    }
}