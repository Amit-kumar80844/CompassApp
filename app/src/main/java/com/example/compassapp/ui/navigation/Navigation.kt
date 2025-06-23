package com.example.compassapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compassapp.ui.compass.CompassScreen
import com.example.compassapp.ui.splash.Splash
import com.example.compassapp.ui.splash.SplashScreen

@Composable
fun Navigate(
    navHostController: NavHostController
) {
    NavHost(navController =navHostController, startDestination = NavGraph.Splash.route) {
       composable(route = NavGraph.Splash.route) {
           Splash(navHostController)
        }
        composable(route = NavGraph.Compass.route) {
            CompassScreen(navHostController)
        }
    }
}
