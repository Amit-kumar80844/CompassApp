package com.example.compassapp.ui.compass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CompassViewModel : ViewModel() {
    var heading by mutableFloatStateOf(0f)
        private set

    var magneticStrength by mutableFloatStateOf(0f)
        private set

    var isDarkTheme by mutableStateOf(false)
        private set

    fun updateHeading(newHeading: Float) {
        heading = newHeading
    }

    fun updateMagneticStrength(newStrength: Float) {
        magneticStrength = newStrength
    }

    fun setDarkTheme(isDark: Boolean) {
        isDarkTheme = isDark
    }
}