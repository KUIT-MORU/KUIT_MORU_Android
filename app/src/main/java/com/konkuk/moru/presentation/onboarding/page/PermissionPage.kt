package com.konkuk.moru.presentation.onboarding.page

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import com.konkuk.moru.R
import com.konkuk.moru.core.component.TopBarLogoWithTitle
import com.konkuk.moru.core.component.button.MoruButtonTypeA
import com.konkuk.moru.presentation.onboarding.component.PermissionItem
import com.konkuk.moru.presentation.onboarding.model.PermissionType
import com.konkuk.moru.ui.theme.MORUTheme.colors
import com.konkuk.moru.ui.theme.MORUTheme.typography
import com.konkuk.moru.presentation.onboarding.OnboardingViewModel

@Composable
fun PermissionPage(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel? = null
) {
    val context = LocalContext.current
    val permissionStates = remember {
        mutableStateMapOf<PermissionType, Boolean>().apply {
            PermissionType.values().forEach { put(it, false) }
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionStates[PermissionType.PUSH_NOTIFICATION] = isGranted
        if (!isGranted) {
            context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            })
        }
    }

    LaunchedEffect(Unit) {
        permissionStates[PermissionType.PUSH_NOTIFICATION] =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        permissionStates[PermissionType.OVERLAY] =
            Settings.canDrawOverlays(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        permissionStates[PermissionType.DO_NOT_DISTURB] =
            notificationManager.isNotificationPolicyAccessGranted

        permissionStates[PermissionType.SCHEDULE_EXACT_ALARM] =
            AlarmManagerCompat.canScheduleExactAlarms(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
    }

    fun handlePermissionClick(type: PermissionType) {
        when (type) {
            PermissionType.PUSH_NOTIFICATION -> {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            PermissionType.OVERLAY -> {
                if (!Settings.canDrawOverlays(context)) {
                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                        data = Uri.parse("package:${context.packageName}")
                    })
                }
            }

            PermissionType.DO_NOT_DISTURB -> {
                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (!manager.isNotificationPolicyAccessGranted) {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                }
            }

            PermissionType.SCHEDULE_EXACT_ALARM -> {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
        }
    }

    val permissions = listOf(
        Triple("푸시 알림 허용", "루틴 실천에 도움되는 알림을 받으세요!", PermissionType.PUSH_NOTIFICATION),
        Triple("시간 알림 허용", "루틴 시간에 맞춰 알림을 받으세요!", PermissionType.SCHEDULE_EXACT_ALARM),
        Triple("다른 앱 위 표시 허용", "루틴 실천 시, 집중하기 위해 필요해요!", PermissionType.OVERLAY),
        Triple("방해 금지 모드 제어 허용", "루틴 실천 중 불필요한 알림을 막아요!", PermissionType.DO_NOT_DISTURB)
    )
    // 임시로 grant 상태를 담는 리스트 사용
    val permissionStatesTest = remember {
        mutableStateMapOf<PermissionType, Boolean>().apply {
            PermissionType.values().forEach { put(it, false) }
        }
    }
    

    val allGranted = permissionStates.values.all { it } // Todo: 실제 권한 상태
    // 임시 권한 허용여부 데이터로 enable 상태 설정
    val allGrantedTest = permissionStatesTest.values.all { it } // 임시로 Test 상태 사용

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.charcoalBlack)
            .systemBarsPadding()
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
                    .padding(bottom = 10.dp, top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "앱 이용을 위해",
                        style = typography.body_SB_24
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "아래 접근 권한 허용이 필요해요",
                        style = typography.body_SB_24
                    )
                    Spacer(modifier = Modifier.height(60.dp))

                    permissions.forEach { (title, desc, type) ->
                        PermissionItem(
                            title = title,
                            description = desc,
                            type = type,
                            //isGranted = permissionStates[type] == true,
                            isGranted = permissionStatesTest[type] == true, // 임시로 Test 상태 사용
                            onClick = {
                                //handlePermissionClick(type)
                                //Todo: 임시로 클릭 시 Test의 grant 상태 변경
                                permissionStatesTest[type] = !permissionStatesTest[type]!!
                            }
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_onboarding_statusbar5),
                        contentDescription = "status bar",
                        modifier = Modifier.width(134.dp)
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    MoruButtonTypeA(
                        text = "다음",
                        //enabled = allGranted,
                        enabled = allGrantedTest, //Todo: 임시 데이터임
                        onClick = onNext
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PermissionPagePreview() {
    PermissionPage(onNext = {})
}