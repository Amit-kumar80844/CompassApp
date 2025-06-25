package com.example.compassapp.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.compassapp.R
import com.example.compassapp.ui.navigation.NavGraph
import com.example.compassapp.ui.theme.CompassAppTheme
import kotlinx.coroutines.delay

@Composable
fun Splash(navigation: NavHostController) {
    LaunchedEffect(true) {
        delay(1500)
        navigation.navigate(NavGraph.Compass.route){
            popUpTo(NavGraph.Splash.route) {
                inclusive = true
            }
        }
    }
    SplashScreen()
}

@Composable
fun SplashScreen() {
    val isDarkTheme = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkTheme) {
                    MaterialTheme.colorScheme.background
                } else {
                    Color.White
            } ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            Image(
                painter = painterResource(R.mipmap.compass),
                contentDescription = "Splash Screen Logo",
                modifier = Modifier.size(200.dp, 200.dp)
                    .clip(CircleShape)
            )
            Text("Compass App",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.padding(30.dp))
        }
    }
}

@Composable
@Preview (showBackground = true)
fun SplashScreenPreview() {
    CompassAppTheme {
        SplashScreen()
    }
}