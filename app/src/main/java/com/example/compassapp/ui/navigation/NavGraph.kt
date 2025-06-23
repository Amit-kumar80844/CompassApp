package com.example.compassapp.ui.navigation

sealed class NavGraph(
    val route: String
) {
    data object Splash : NavGraph("splash")
    data object Compass : NavGraph("compass")
}