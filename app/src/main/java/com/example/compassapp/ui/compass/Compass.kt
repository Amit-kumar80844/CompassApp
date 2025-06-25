package com.example.compassapp.ui.compass

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.compassapp.data.CompassViewModelFactory
import com.example.compassapp.data.SensorRepository
import com.example.compassapp.ui.theme.CompassAppTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassScreen(
    navHostController: NavHostController,
) {
    val context = LocalContext.current
    val sensorRepository = remember { SensorRepository(context) }
    val viewModel: CompassViewModel = viewModel(
        factory = CompassViewModelFactory(sensorRepository)
    )
    val state = viewModel.uiState.collectAsState()
    CompassComponent(
        heading = state.value.heading,
        magneticStrength = state.value.magneticStrength,
        isDarkTheme = state.value.isDarkTheme
    )
}


@Composable
fun CompassComponent(
    heading: Float = 0f,
    magneticStrength: Float = 0f,
    isDarkTheme: Boolean = true
) {
    val direction = remember(heading) {
        when (heading) {
            in 0f..22.5f, in 337.5f..360f -> "North"
            in 22.5f..67.5f -> "Northeast"
            in 67.5f..112.5f -> "East"
            in 112.5f..157.5f -> "Southeast"
            in 157.5f..202.5f -> "South"
            in 202.5f..247.5f -> "Southwest"
            in 247.5f..292.5f -> "West"
            else -> "Northwest"
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RotatingCompassDial(
                heading = heading + 90f,
                isDarkTheme = isDarkTheme
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("${heading.toInt()}°", fontSize = 32.sp, color = textColor)
            Text(direction, fontSize = 20.sp, color = textColor)
            Text("Magnetic Strength $magneticStrength µT", fontSize = 14.sp, color = textColor)
        }
    }
}

@Composable
fun RotatingCompassDial(heading: Float, isDarkTheme: Boolean) {
    val dialColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // This Canvas contains the rotating dial elements (tick marks, numbers, cardinal directions)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(degrees = -heading) // Rotate the entire dial based on heading
        ) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Paint for degree numbers
            val degreeTextPaint = Paint().apply {
                color = textColor.toArgb()
                textSize = 30f // Adjust font size for numbers
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

            // Draw tick marks and degree numbers
            for (i in 0 until 360 step 15) { // Smaller steps for more ticks if desired
                val angleDegrees = i.toFloat()
                val angleRad = Math.toRadians(angleDegrees.toDouble()).toFloat()

                // Determine tick length based on major/minor degrees
                val isMajorTick = (i % 30 == 0) // Every 30 degrees for numbers
                val tickLength = if (isMajorTick) 40f else 20f // Longer for major ticks

                // Draw tick marks
                val tickStartRadius = radius - tickLength
                val tickEndRadius = radius

                drawLine(
                    dialColor,
                    start = Offset(center.x + cos(angleRad) * tickStartRadius, center.y + sin(angleRad) * tickStartRadius),
                    end = Offset(center.x + cos(angleRad) * tickEndRadius, center.y + sin(angleRad) * tickEndRadius),
                    strokeWidth = 2f
                )

                if (isMajorTick) {
                    val textRadius = radius - 65f // Adjust to place numbers further in
                    val textX = center.x + cos(angleRad) * textRadius
                    val textY = center.y + sin(angleRad) * textRadius

                    drawContext.canvas.nativeCanvas.save()
                    drawContext.canvas.nativeCanvas.translate(textX, textY)
                    // Rotate the text so its bottom is towards the center of the dial
                    // For example, 0° (North) needs no rotation relative to its point if drawn horizontally
                    // 90° (East) needs 90 degree clockwise rotation etc.
                    drawContext.canvas.nativeCanvas.rotate(angleDegrees) // Rotate the text itself
                    drawContext.canvas.nativeCanvas.drawText(
                        "${i}°",
                        0f, // X relative to translated origin
                        degreeTextPaint.textSize / 3, // Y adjusted for baseline relative to translated origin
                        degreeTextPaint
                    )
                    drawContext.canvas.nativeCanvas.restore() // Restore canvas state
                }
            }

            // Paint for Cardinal Directions (N, E, S, W)
            val cardinalTextPaint = Paint().apply {
                color = textColor.toArgb()
                textSize = 50f // Larger for cardinal directions
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

            val cardinalTextRadius = radius - 120f // Position further inside the numbers

            // Function to draw a cardinal direction
            fun drawCardinal(cardinal: String, angle: Float) {
                val cardinalAngleRad = Math.toRadians(angle.toDouble()).toFloat()
                val cardinalTextX = center.x + cos(cardinalAngleRad) * cardinalTextRadius
                val cardinalTextY = center.y + sin(cardinalAngleRad) * cardinalTextRadius

                drawContext.canvas.nativeCanvas.save()
                drawContext.canvas.nativeCanvas.translate(cardinalTextX, cardinalTextY)
                drawContext.canvas.nativeCanvas.rotate(angle) // Rotate the cardinal letter
                drawContext.canvas.nativeCanvas.drawText(
                    cardinal,
                    0f,
                    cardinalTextPaint.textSize / 3,
                    cardinalTextPaint
                )
                drawContext.canvas.nativeCanvas.restore()
            }

            drawCardinal("N", 0f)    // North
            drawCardinal("E", 90f)   // East
            drawCardinal("S", 180f)  // South
            drawCardinal("W", 270f)  // West
        }

        // Fixed arrow pointer, always pointing to the top of the screen
        // This is drawn OVER the rotating dial
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val arrowColor = if (isDarkTheme) Color.Black else Color.Red
            val redDotColor = if (isDarkTheme) Color.Red else Color.Transparent

            val path = Path().apply {
                moveTo(center.x, center.y - 70) // Arrow top point
                lineTo(center.x - 20, center.y + 20) // Left base point
                lineTo(center.x + 20, center.y + 20) // Right base point
                close()
            }
            // Arrow is fixed, pointing up
            drawPath(path, color = arrowColor)

            // Small circle at the base of the arrow
            drawCircle(redDotColor, radius = 6f, center = Offset(center.x, center.y + 30))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompassLightPreview() {
    CompassAppTheme {
        CompassComponent()
    }
}