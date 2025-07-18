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

    // 루틴 시작 전 소개 화면
    data object RoutineFocusIntro : Route("routine_focus_intro")

    // 실제 루틴 실행 화면 (집중 루틴 시-몰입화면)
    data object RoutineFocus : Route("routine_focus")
    // 실제 루틴 실행 화면 (간편 루틴 화면)
    data object RoutineSimpleRun : Route("routine_simple_run")

    data object RoutineFeed : Route(route = "routine_feed")
    data object RoutineFeedDetail : Route(route = "routine_feed_detail/{routineId}") {
        fun createRoute(routineId: Int) = "routine_feed_detail/$routineId"
    }

    data object RoutineFeedRec : Route(route = "routine_feed_rec/{title}") {
        fun createRoute(title: String): String {
            val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            return "routine_feed_rec/$encodedTitle"
        }
    }

    data object MyRoutine : Route(route = "my_routine")

    data object MyActivity : Route(route = "my_activity")

    data object Notification : Route("notification")

    data object ActSetting : Route(route = "act_setting")
    data object ActScrab : Route(route = "act_scrab")
    data object ActFabTag : Route(route = "act_fab_tag")
    data object ActProfile : Route(route = "act_profile")
    data object ActRecord : Route(route = "act_record")
}