package com.example.compassapp.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compassapp.data.OrientationAngles
import com.example.compassapp.data.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable

@Serializable
data class CompassState(
    val heading: Float = 0f,
    val magneticStrength: Float = 0f,
    val isDarkTheme: Boolean = false
)

class CompassViewModel(
    private val sensorRepository: SensorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompassState())
    val uiState: StateFlow<CompassState> = _uiState

    init {
        sensorRepository.orientationFlow()
            .onEach { orientation ->
                _uiState.value = _uiState.value.copy(heading = orientation.azimuth)
            }
            .launchIn(viewModelScope)

        sensorRepository.magneticStrengthFlow()
            .onEach { strength ->
                _uiState.value = _uiState.value.copy(magneticStrength = strength)
            }
            .launchIn(viewModelScope)
    }
}
