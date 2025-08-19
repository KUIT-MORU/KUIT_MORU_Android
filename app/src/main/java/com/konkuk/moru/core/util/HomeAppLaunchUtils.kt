package com.konkuk.moru.core.util

import android.content.Context
import android.content.Intent

/**
 * 앱 실행을 위한 공통 유틸리티
 */
object HomeAppLaunchUtils {
    
    /**
     * 앱을 실행하는 함수
     * @param context 컨텍스트
     * @param packageName 실행할 앱의 패키지명
     * @param tag 로그 태그 (호출하는 화면명)
     */
    fun launchApp(context: Context, packageName: String, tag: String = "AppLaunchUtils") {
        android.util.Log.d(tag, "🚀 launchApp 호출됨: packageName=$packageName")
        
        try {
            // 1. 패키지가 설치되어 있는지 확인
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            val appInfo = packageInfo.applicationInfo
            val appLabel = appInfo?.loadLabel(context.packageManager)?.toString() ?: "Unknown"
            android.util.Log.d(tag, "✅ 패키지 정보 확인됨: $appLabel")
            
            // 2. 앱이 비활성화되었는지 확인
            if (appInfo != null && !appInfo.enabled) {
                android.util.Log.e(tag, "❌ 앱이 비활성화됨: $packageName ($appLabel)")
                return
            }
            
            // 3. 시스템 앱인지 확인 (참고용)
            val isSystemApp = (appInfo?.flags ?: 0) and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0
            android.util.Log.d(tag, "📱 앱 타입: ${if (isSystemApp) "시스템 앱" else "사용자 앱"}")
            
            // 4. Launch Intent 생성 시도
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                android.util.Log.d(tag, "✅ Launch Intent 생성 성공: $packageName")
                android.util.Log.d(tag, "🎯 Intent 상세정보: ${intent.component?.className}")
                
                // 5. Intent 플래그 설정
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                
                // 6. 앱 실행 시도
                context.startActivity(intent)
                android.util.Log.d(tag, "✅ 앱 실행 성공: $packageName ($appLabel)")
                
            } else {
                // Launch Intent가 null인 이유 분석
                android.util.Log.e(tag, "❌ Launch Intent 생성 실패: $packageName ($appLabel)")
                
                // 추가 분석: 다른 방법으로 실행 가능한지 확인
                val mainIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    setPackage(packageName)
                }
                val resolveInfos = context.packageManager.queryIntentActivities(mainIntent, 0)
                
                if (resolveInfos.isNotEmpty()) {
                    android.util.Log.w(tag, "⚠️ LAUNCHER 액티비티는 존재함: ${resolveInfos.size}개")
                    resolveInfos.forEach { resolveInfo ->
                        android.util.Log.w(tag, "   - ${resolveInfo.activityInfo.name}")
                    }
                } else {
                    android.util.Log.e(tag, "❌ LAUNCHER 액티비티가 없음: $packageName")
                }
            }
            
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            android.util.Log.e(tag, "❌ 패키지를 찾을 수 없음: $packageName", e)
        } catch (e: android.content.ActivityNotFoundException) {
            android.util.Log.e(tag, "❌ 액티비티를 찾을 수 없음: $packageName", e)
        } catch (e: SecurityException) {
            android.util.Log.e(tag, "❌ 보안 권한 오류: $packageName", e)
        } catch (e: Exception) {
            android.util.Log.e(tag, "❌ 앱 실행 중 예상치 못한 오류: $packageName", e)
            e.printStackTrace()
        }
    }
}
