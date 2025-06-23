package com.example.compassapp.data

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun isDarkMode(): Boolean {
    return isSystemInDarkTheme()
}