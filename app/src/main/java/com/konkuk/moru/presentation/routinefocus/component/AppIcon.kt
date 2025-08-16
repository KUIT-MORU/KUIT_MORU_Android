package com.konkuk.moru.presentation.routinefocus.component

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.konkuk.moru.R
import com.konkuk.moru.presentation.routinefeed.data.AppDto

@Composable
fun AppIcon(
    app: AppDto,
    modifier: Modifier = Modifier
) {
    // 강제 테스트 로그 - 컴포넌트 진입 확인
    android.util.Log.e("TEST_LOG", "🔥 AppIcon 컴포넌트 진입: ${app.name} (${app.packageName})")
    System.out.println("🔥 System.out: AppIcon 컴포넌트 진입 - ${app.name}")
    
    // 기본 로그 추가
    android.util.Log.d("AppIcon", "🎨 AppIcon 렌더링: ${app.name} (${app.packageName})")
    
    // 컴포넌트 시작 로그
    android.util.Log.e("TEST_LOG", "🔥 AppIcon 렌더링 시작: ${app.name} (${app.packageName})")
    
    val context = LocalContext.current
    
    // 실제 앱 아이콘을 가져오는 로직
    val appIcon = remember(app.packageName) {
        // 강제 테스트 로그 - remember 블록 진입 확인
        android.util.Log.e("TEST_LOG", "🔥 AppIcon remember 블록 진입: ${app.name} (${app.packageName})")
        System.out.println("🔥 System.out: AppIcon remember 블록 진입 - ${app.name}")
        
        try {
            android.util.Log.e("TEST_LOG", "🔄 앱 아이콘 로딩 시작: ${app.name} (${app.packageName})")
            Log.d("AppIcon", "🔄 앱 아이콘 로딩 시작: ${app.name} (${app.packageName})")
            
            val packageManager = context.packageManager
            android.util.Log.e("TEST_LOG", "📱 PackageManager 가져옴: ${packageManager != null}")
            
            // 디바이스에 설치된 모든 앱 패키지명 로깅 (디버깅용)
            try {
                val installedApps = packageManager.getInstalledApplications(0)
                android.util.Log.e("TEST_LOG", "📱 디바이스에 설치된 앱 개수: ${installedApps.size}")
                
                // 더 넓은 범위로 검색 (소셜 미디어, 메신저, 브라우저 등)
                val relatedApps = installedApps.filter { appInfo ->
                    val packageName = appInfo.packageName.lowercase()
                    val label = appInfo.loadLabel(packageManager).toString().lowercase()
                    
                    packageName.contains("kakao") || 
                    packageName.contains("naver") || 
                    packageName.contains("instagram") || 
                    packageName.contains("youtube") ||
                    packageName.contains("google") ||
                    packageName.contains("facebook") ||
                    packageName.contains("twitter") ||
                    packageName.contains("whatsapp") ||
                    packageName.contains("telegram") ||
                    packageName.contains("line") ||
                    packageName.contains("wechat") ||
                    packageName.contains("snapchat") ||
                    packageName.contains("tiktok") ||
                    packageName.contains("discord") ||
                    packageName.contains("slack") ||
                    packageName.contains("zoom") ||
                    packageName.contains("teams") ||
                    packageName.contains("chrome") ||
                    packageName.contains("firefox") ||
                    packageName.contains("samsung") ||
                    packageName.contains("samsung internet") ||
                    label.contains("카카오") ||
                    label.contains("네이버") ||
                    label.contains("인스타") ||
                    label.contains("유튜브") ||
                    label.contains("페이스북") ||
                    label.contains("트위터") ||
                    label.contains("왓츠앱") ||
                    label.contains("텔레그램") ||
                    label.contains("라인") ||
                    label.contains("위챗") ||
                    label.contains("스냅챗") ||
                    label.contains("틱톡") ||
                    label.contains("디스코드") ||
                    label.contains("슬랙") ||
                    label.contains("줌") ||
                    label.contains("팀즈") ||
                    label.contains("크롬") ||
                    label.contains("파이어폭스") ||
                    label.contains("삼성")
                }
                
                android.util.Log.e("TEST_LOG", "🔍 관련 앱들:")
                relatedApps.forEach { appInfo ->
                    android.util.Log.e("TEST_LOG", "   - ${appInfo.packageName} (${appInfo.loadLabel(packageManager)})")
                }
                
                // 디버깅을 위해 모든 설치된 앱의 패키지명과 이름 로깅 (처음 20개만)
                android.util.Log.e("TEST_LOG", "📱 모든 설치된 앱 (처음 20개):")
                installedApps.take(20).forEach { appInfo ->
                    android.util.Log.e("TEST_LOG", "   - ${appInfo.packageName} (${appInfo.loadLabel(packageManager)})")
                }
                if (installedApps.size > 20) {
                    android.util.Log.e("TEST_LOG", "   ... 그리고 ${installedApps.size - 20}개 더")
                }
                
                // 사용자 설치 앱만 필터링하여 로깅 (시스템 앱 제외)
                val userInstalledApps = installedApps.filter { appInfo ->
                    (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0
                }
                android.util.Log.e("TEST_LOG", "📱 사용자 설치 앱 (${userInstalledApps.size}개):")
                userInstalledApps.forEach { appInfo ->
                    android.util.Log.e("TEST_LOG", "   - ${appInfo.packageName} (${appInfo.loadLabel(packageManager)})")
                }
            } catch (e: Exception) {
                android.util.Log.e("TEST_LOG", "❌ 설치된 앱 목록 조회 실패: ${e.message}")
            }
            
            // 패키지가 설치되어 있는지 먼저 확인
            try {
                val packageInfo = packageManager.getPackageInfo(app.packageName, 0)
                android.util.Log.e("TEST_LOG", "📦 패키지 정보 확인: ${packageInfo.packageName}")
            } catch (e: Exception) {
                android.util.Log.e("TEST_LOG", "❌ 패키지 정보 확인 실패: ${app.packageName} - ${e.message}")
                throw e
            }
            
            val appInfo = packageManager.getApplicationInfo(app.packageName, 0)
            android.util.Log.e("TEST_LOG", "✅ ApplicationInfo 로드 성공: ${appInfo.name}")
            Log.d("AppIcon", "✅ ApplicationInfo 로드 성공: ${appInfo.name}")
            
            val drawable = packageManager.getApplicationIcon(appInfo)
            android.util.Log.e("TEST_LOG", "✅ Drawable 로드 성공: ${drawable.intrinsicWidth}x${drawable.intrinsicHeight}")
            Log.d("AppIcon", "✅ Drawable 로드 성공: ${drawable.intrinsicWidth}x${drawable.intrinsicHeight}")
            
            val bitmap = drawableToBitmap(drawable)
            android.util.Log.e("TEST_LOG", "✅ Bitmap 변환 성공: ${bitmap.width}x${bitmap.height}")
            Log.d("AppIcon", "✅ Bitmap 변환 성공: ${bitmap.width}x${bitmap.height}")
            
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            android.util.Log.e("TEST_LOG", "❌ 앱 아이콘 로딩 실패: ${app.name} (${app.packageName})")
            android.util.Log.e("TEST_LOG", "❌ 오류 타입: ${e.javaClass.simpleName}")
            android.util.Log.e("TEST_LOG", "❌ 오류 메시지: ${e.message}")
            android.util.Log.e("TEST_LOG", "❌ 상세 오류: ${e}")
            Log.e("AppIcon", "❌ 앱 아이콘 로딩 실패: ${app.name} (${app.packageName})", e)
            
            // 대안적인 방법: 앱 이름으로 검색
            try {
                android.util.Log.e("TEST_LOG", "🔄 대안 방법 시도: 앱 이름으로 검색")
                val fallbackPackageManager = context.packageManager
                val installedApps = fallbackPackageManager.getInstalledApplications(0)
                val matchingApp = installedApps.find { appInfo ->
                    val label = appInfo.loadLabel(fallbackPackageManager).toString().lowercase()
                    val packageName = appInfo.packageName.lowercase()
                    val searchName = app.name.lowercase()
                    
                    // 매칭 시도는 처음 10개만 로깅 (너무 많은 로그 방지)
                    if (installedApps.indexOf(appInfo) < 10) {
                        android.util.Log.e("TEST_LOG", "🔍 매칭 시도: '${searchName}' vs '${label}' (${packageName})")
                    }
                    
                    // 정확한 매칭
                    label == searchName || packageName == searchName ||
                    // 부분 매칭
                    label.contains(searchName) || packageName.contains(searchName) ||
                    // 한국어 앱명 매칭
                    when (searchName) {
                        "카카오톡" -> label.contains("카카오") || packageName.contains("kakao")
                        "네이버" -> label.contains("네이버") || packageName.contains("naver")
                        "인스타그램" -> label.contains("인스타") || packageName.contains("instagram")
                        "유튜브" -> label.contains("유튜브") || packageName.contains("youtube")
                        else -> false
                    }
                }
                
                if (matchingApp != null) {
                    android.util.Log.e("TEST_LOG", "✅ 대안 방법 성공: ${matchingApp.packageName}")
                    val drawable = fallbackPackageManager.getApplicationIcon(matchingApp)
                    val bitmap = drawableToBitmap(drawable)
                    bitmap.asImageBitmap()
                } else {
                    android.util.Log.e("TEST_LOG", "❌ 대안 방법도 실패: ${app.name}과 일치하는 앱 없음")
                    null
                }
            } catch (e2: Exception) {
                android.util.Log.e("TEST_LOG", "❌ 대안 방법도 실패: ${e2.message}")
                null
            }
        }
    }
    
    if (appIcon != null) {
        Image(
            bitmap = appIcon,
            contentDescription = app.name,
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                    // 앱 실행
                    launchApp(context, app.packageName)
                }
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_default),
            contentDescription = app.name,
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                    // 앱이 설치되어 있지 않을 때 메시지 표시
                    showAppNotInstalledMessage(context, app.name)
                }
        )
    }
}

// Drawable을 Bitmap으로 변환하는 헬퍼 함수
private fun drawableToBitmap(drawable: Drawable): Bitmap {
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

// 앱을 실행하는 헬퍼 함수
private fun launchApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        // 앱 실행 실패 시 로그 출력
        android.util.Log.e("AppIcon", "앱 실행 실패: $packageName", e)
    }
}

// 앱이 설치되어 있지 않을 때 메시지를 표시하는 헬퍼 함수
private fun showAppNotInstalledMessage(context: Context, appName: String) {
    try {
        // Toast 메시지로 간단한 알림
        android.widget.Toast.makeText(
            context,
            "$appName 앱이 설치되어 있지 않습니다.",
            android.widget.Toast.LENGTH_SHORT
        ).show()
        
        // 로그에도 기록
        android.util.Log.e("TEST_LOG", "❌ 앱 미설치: $appName")
    } catch (e: Exception) {
        android.util.Log.e("AppIcon", "메시지 표시 실패", e)
    }
}
