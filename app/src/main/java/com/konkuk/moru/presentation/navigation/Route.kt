package com.konkuk.moru.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Route(
    val route: String
) {
    data object AuthCheck : Route("auth_check")
    data object Login : Route("login")
    data object SignUp : Route("sign_up")
    data object Main : Route("main")
    data object Onboarding : Route("onboarding")

    data object Home : Route(route = "home")

    data object RoutineFeed : Route(route = "routine_feed")

    data object RoutineSearch : Route("routine_search")
    data object RoutineFeedDetail : Route(route = "routine_feed_detail/{routineId}") {
        fun createRoute(routineId: Int) = "routine_feed_detail/$routineId"
    }

    data object RoutineFeedRec : Route(route = "routine_feed_rec/{title}") {
        fun createRoute(title: String): String {
            val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            return "routine_feed_rec/$encodedTitle"
        }
    }

    object Follow : Route("follow/{userId}/{selectedTab}") { // [수정] userId 추가
        fun createRoute(userId: Int, selectedTab: String) = "follow/$userId/$selectedTab"
    }

    // [추가] UserProfileScreen 경로 정의 (어떤 유저의 프로필인지 'userId' 파라미터 추가)
    object UserProfile : Route("user_profile/{userId}") {
        fun createRoute(userId: Int) = "user_profile/$userId"
    }

    data object MyRoutine : Route(route = "my_routine")

    data object MyActivity : Route(route = "my_activity")

    data object Notification : Route("notification")

    data object ActSetting : Route(route = "act_setting")
    data object ActScrab : Route(route = "act_scrab")
    data object ActFabTag : Route(route = "act_fab_tag")
    data object ActProfile : Route(route = "act_profile")
    data object ActRecord : Route(route = "act_record")
    data object ActRecordDetail : Route(route = "act_record_detail/{routineTitle}") {
        fun createRoute(routineTitle: String): String {
            val encoded = URLEncoder.encode(routineTitle, StandardCharsets.UTF_8.toString())
            return "act_record_detail/$encoded"
        }
    }
}