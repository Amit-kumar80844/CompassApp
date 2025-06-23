package com.example.compassapp.ui.splash

import android.os.Handler
import android.os.Looper
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.compassapp.R
import com.example.compassapp.ui.navigation.NavGraph
import com.example.compassapp.ui.theme.CompassAppTheme

@Composable
fun Splash(navigation : NavHostController){
    SplashScreen()
    Handler(Looper.getMainLooper()).postDelayed({
        navigation.navigate(NavGraph.Compass)
    },1500) // 1.5 seconds delay only for splash screen
}

@Composable
fun SplashScreen() {
    val isDarkTheme = isSystemInDarkTheme()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            Image(
                painter = painterResource(
                    if (isDarkTheme) R.drawable.splashdark else R.drawable.splashday),
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