package com.example.compassapp.data

import android.content.Context
import android.hardware.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

data class OrientationAngles(
    val azimuth: Float,
    val pitch: Float,
    val roll: Float,
    val acceleration: Float // Linear acceleration magnitude
)

class SensorRepository(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val gravityReading = FloatArray(3)           // from TYPE_ACCELEROMETER
    private val magnetometerReading = FloatArray(3)
    private val linearAcceleration = FloatArray(3)       // from TYPE_LINEAR_ACCELERATION
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    fun orientationFlow(): Flow<OrientationAngles> = callbackFlow {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, gravityReading, 0, 3)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, magnetometerReading, 0, 3)
                    }
                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        System.arraycopy(event.values, 0, linearAcceleration, 0, 3)
                    }
                }

                val success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    gravityReading,
                    magnetometerReading
                )

                if (success) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)

                    // Calculate total linear acceleration magnitude
                    val totalLinearAcceleration = sqrt(
                        linearAcceleration[0] * linearAcceleration[0] +
                                linearAcceleration[1] * linearAcceleration[1] +
                                linearAcceleration[2] * linearAcceleration[2]
                    )

                    trySend(
                        OrientationAngles(
                            azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat(),
                            pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat(),
                            roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat(),
                            acceleration = totalLinearAcceleration
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Register all required sensors
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }

        awaitClose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    fun magneticStrengthFlow(): Flow<Float> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    val strength = sqrt(
                        event.values[0] * event.values[0] +
                                event.values[1] * event.values[1] +
                                event.values[2] * event.values[2]
                    )
                    trySend(strength)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
