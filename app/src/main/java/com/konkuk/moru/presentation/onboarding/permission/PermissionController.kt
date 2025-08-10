package com.konkuk.moru.presentation.onboarding.permission

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.konkuk.moru.presentation.onboarding.model.PermissionType

class PermissionController(
    private val context: Context,
    private val notificationLauncher: ManagedActivityResultLauncher<String, Boolean>,
    private val isPreview: Boolean
) {
    val states = mutableStateMapOf<PermissionType, Boolean>().apply {
        PermissionType.entries.forEach { put(it, false) }
    }

    val allGranted: Boolean
        get() = states.values.all { it }

    fun onClick(type: PermissionType) {
        when (type) {
            PermissionType.PUSH_NOTIFICATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    if (!areNotificationsEnabledCompat(context)) {
                        context.startActivity(
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        )
                    }
                }
            }

            PermissionType.OVERLAY -> {
                if (!android.provider.Settings.canDrawOverlays(context)) {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            "package:${context.packageName}".toUri()
                        )
                    )
                }
            }

            PermissionType.DO_NOT_DISTURB -> {
                val nm =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (!nm.isNotificationPolicyAccessGranted) {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                }
            }

            PermissionType.SCHEDULE_EXACT_ALARM -> {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !AlarmManagerCompat.canScheduleExactAlarms(alarmManager)
                ) {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
        }
    }

    fun refresh() {
        if (isPreview) return // 프리뷰에선 시스템 접근 금지

        // 1) 푸시 알림
        val pushGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            areNotificationsEnabledCompat(context)
        }
        states[PermissionType.PUSH_NOTIFICATION] = pushGranted

        // 2) 오버레이
        states[PermissionType.OVERLAY] = Settings.canDrawOverlays(context)

        // 3) DND
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        states[PermissionType.DO_NOT_DISTURB] = nm.isNotificationPolicyAccessGranted

        // 4) 정확한 알람
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val exactGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManagerCompat.canScheduleExactAlarms(alarmManager)
        } else {
            true
        }
        states[PermissionType.SCHEDULE_EXACT_ALARM] = exactGranted
    }

    private fun areNotificationsEnabledCompat(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}

@Composable
fun rememberPermissionController(): PermissionController {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 알림 런처: 콜백에서 상태 반영 + 설정 이동
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 콜백 시점에는 컨트롤러가 만들어져 있어야 하므로 아래에서 refresh로 커버
        // (별도 로직 없이도 refresh가 상태를 정확히 맞춤)
    }

    val controller = remember(context, isPreview) {
        PermissionController(context, notificationLauncher, isPreview)
    }

    // 최초 진입 때 갱신
    LaunchedEffect(Unit) {
        controller.refresh()
    }

    // 설정에서 복귀 시 갱신
    DisposableEffect(lifecycleOwner) {
        if (!isPreview) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    controller.refresh()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        } else {
            onDispose { /* no-op */ }
        }
    }

    return controller
}