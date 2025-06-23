package com.example.compassapp.data

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

// Data class for compass readings
data class CompassReading(
    val azimuth: Float = 0f,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val magneticStrength: Float = 0f,
    val accuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE
) {
    fun getHeadingDegrees(): Float {
        return Math.toDegrees(azimuth.toDouble()).toFloat()
    }

    fun getFormattedHeading(): String {
        val degrees = getHeadingDegrees()
        val normalizedDegrees = (degrees + 360) % 360
        return "${normalizedDegrees.roundToInt()}°"
    }

    fun getCardinalDirection(): String {
        val degrees = (getHeadingDegrees() + 360) % 360
        return when {
            degrees < 22.5 || degrees >= 337.5 -> "N"
            degrees < 67.5 -> "NE"
            degrees < 112.5 -> "E"
            degrees < 157.5 -> "SE"
            degrees < 202.5 -> "S"
            degrees < 247.5 -> "SW"
            degrees < 292.5 -> "W"
            degrees < 337.5 -> "NW"
            else -> "N"
        }
    }

    fun getMagneticStrengthDescription(): String {
        return when {
            magneticStrength < 25 -> "Weak"
            magneticStrength < 50 -> "Fair"
            magneticStrength < 75 -> "Good"
            else -> "Strong"
        }
    }

    fun isAccurate(): Boolean {
        return accuracy >= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
    }
}

// UI State data class
data class CompassUiState(
    val compassReading: CompassReading = CompassReading(),
    val isCalibrating: Boolean = false,
    val isDarkTheme: Boolean = false,
    val sensorAvailable: Boolean = true,
    val errorMessage: String? = null
)

class CompassViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager: SensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    // Sensor data arrays
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    // State management
    private val _uiState = MutableStateFlow(CompassUiState())
    val uiState: StateFlow<CompassUiState> = _uiState.asStateFlow()

    private var updateJob: Job? = null
    private var isListening = false

    // Smoothing variables for better compass stability
    private var smoothedAzimuth = 0f
    private val smoothingFactor = 0.1f

    init {
        checkSensorAvailability()
    }

    private fun checkSensorAvailability() {
        val hasAccelerometer = accelerometer != null
        val hasMagnetometer = magnetometer != null

        if (!hasAccelerometer || !hasMagnetometer) {
            _uiState.value = _uiState.value.copy(
                sensorAvailable = false,
                errorMessage = "Required sensors not available"
            )
        }
    }

    fun startSensorListening() {
        if (isListening) return

        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        magnetometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        isListening = true
        startPeriodicUpdates()
    }

    fun stopSensorListening() {
        if (!isListening) return

        sensorManager.unregisterListener(this)
        isListening = false
        updateJob?.cancel()
    }

    private fun startPeriodicUpdates() {
        updateJob = viewModelScope.launch {
            while (isListening) {
                updateOrientationAngles()
                delay(50) // Update every 50ms for smooth UI
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)

                // Calculate magnetic field strength
                val strength = sqrt(
                    event.values[0] * event.values[0] +
                            event.values[1] * event.values[1] +
                            event.values[2] * event.values[2]
                )

                // Update magnetic strength in the current reading
                val currentReading = _uiState.value.compassReading
                _uiState.value = _uiState.value.copy(
                    compassReading = currentReading.copy(magneticStrength = strength)
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        val currentReading = _uiState.value.compassReading
        _uiState.value = _uiState.value.copy(
            compassReading = currentReading.copy(accuracy = accuracy),
            isCalibrating = accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW
        )
    }

    private fun updateOrientationAngles() {
        // Check if we have valid readings
        if (accelerometerReading.all { it == 0f } || magnetometerReading.all { it == 0f }) {
            return
        }

        // Get rotation matrix
        val success = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        if (success) {
            // Get orientation angles
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // Apply smoothing to azimuth for stability
            val rawAzimuth = orientationAngles[0]
            smoothedAzimuth = smoothAzimuth(smoothedAzimuth, rawAzimuth, smoothingFactor)

            // Update the compass reading
            val newReading = CompassReading(
                azimuth = smoothedAzimuth,
                pitch = orientationAngles[1],
                roll = orientationAngles[2],
                magneticStrength = _uiState.value.compassReading.magneticStrength,
                accuracy = _uiState.value.compassReading.accuracy
            )

            _uiState.value = _uiState.value.copy(compassReading = newReading)
        }
    }

    private fun smoothAzimuth(current: Float, target: Float, factor: Float): Float {
        var diff = target - current

        // Handle the wrap-around at ±π
        if (diff > PI) {
            diff -= 2 * PI.toFloat()
        } else if (diff < -PI) {
            diff += 2 * PI.toFloat()
        }

        return current + factor * diff
    }

    fun setDarkTheme(isDark: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkTheme = isDark)
    }

    fun calibrateCompass() {
        _uiState.value = _uiState.value.copy(isCalibrating = true)

        // Reset smoothing
        smoothedAzimuth = 0f

        viewModelScope.launch {
            delay(3000) // Simulate calibration time
            _uiState.value = _uiState.value.copy(isCalibrating = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopSensorListening()
    }
}