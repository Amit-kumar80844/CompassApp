package com.example.compassapp.data

import android.content.Context
import android.hardware.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow

data class OrientationAngles(val azimuth: Float, val pitch: Float, val roll: Float)

class SensorRepository(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    fun orientationFlow(): Flow<OrientationAngles> = callbackFlow {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, accelerometerReading, 0, 3)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, magnetometerReading, 0, 3)
                    }
                }

                val success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerReading,
                    magnetometerReading
                )

                if (success) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    trySend(
                        OrientationAngles(
                            azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat(),
                            pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat(),
                            roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Register listeners
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
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
                    val strength = kotlin.math.sqrt(
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
