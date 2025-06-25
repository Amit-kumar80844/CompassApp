package com.example.compassapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compassapp.ui.compass.CompassScreen
import com.example.compassapp.ui.splash.Splash

@Composable
fun Navigate(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = NavGraph.Compass.route) {
       composable(route = NavGraph.Splash.route) {
           Splash(navHostController)
        }
        composable(route = NavGraph.Compass.route) {
            CompassScreen(navHostController)
        }
    }
}
