package com.konkuk.moru.presentation.navigation

sealed class Route(
    val route: String
) {
    data object Home : Route(route = "home")

    data object RoutineFeed : Route(route = "routine_feed")

    data object MyRoutine : Route(route = "my_routine")

    data object MyActivity : Route(route = "my_activity")
}