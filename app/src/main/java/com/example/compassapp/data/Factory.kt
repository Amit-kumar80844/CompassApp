package com.example.compassapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compassapp.ui.compass.CompassViewModel


class CompassViewModelFactory(
    private val sensorRepository: SensorRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompassViewModel::class.java)) {
            return CompassViewModel(sensorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

