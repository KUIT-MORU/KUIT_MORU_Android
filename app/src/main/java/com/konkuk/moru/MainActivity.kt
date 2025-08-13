package com.konkuk.moru

import android.os.Bundle
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
            focusViewModel.showScreenBlockPopup(focusViewModel.selectedApps)
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (focusViewModel.isFocusRoutineActive && !focusViewModel.isPermittedAppLaunch) {
            focusViewModel.showScreenBlockPopup(focusViewModel.selectedApps)
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
                    navController = navController
                )
            }
        }
    }
}