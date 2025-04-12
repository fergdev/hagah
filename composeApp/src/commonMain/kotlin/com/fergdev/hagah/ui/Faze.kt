package com.fergdev.hagah.ui

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fergdev.hagah.LocalHazeState
import com.fergdev.hagah.ui.FazeDirection.Down
import com.fergdev.hagah.ui.FazeDirection.Left
import com.fergdev.hagah.ui.FazeDirection.Right
import com.fergdev.hagah.ui.FazeDirection.Up
import dev.chrisbanes.haze.HazeProgressive.Companion.horizontalGradient
import dev.chrisbanes.haze.HazeProgressive.Companion.verticalGradient
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

private val DefaultBlurRadius = 100.dp

@Composable
fun Modifier.faze(containerColor: Color = fazeContainerColor()): Modifier =
    this.clip(shape = RoundedCornerShape(16.dp))
        .hazeEffect(
            state = LocalHazeState.current,
            style = HazeStyle(
                noiseFactor = 0f,
                blurRadius = DefaultBlurRadius,
                backgroundColor = Color.Transparent,
                tint = HazeTint(color = containerColor),
            )
        )

enum class FazeDirection {
    Up, Down, Left, Right
}

private const val DefaultStartIntensity = 0.7f
private const val DefaultEndIntensity = 0.000003f

@Composable
fun Modifier.fazeGradient(
    direction: FazeDirection,
    easing: Easing = LinearEasing
): Modifier =
    this.hazeEffect(
        state = LocalHazeState.current,
        style = HazeStyle(
            blurRadius = DefaultBlurRadius,
            backgroundColor = Color.Transparent,
            tint = HazeTint(fazeContainerColor()),
        )
    ) {
        progressive =
            when (direction) {
                Up -> verticalGradient(
                    easing = easing,
                    startIntensity = DefaultStartIntensity,
                    endIntensity = DefaultEndIntensity
                )

                Down -> verticalGradient(
                    easing = easing,
                    startIntensity = DefaultEndIntensity,
                    endIntensity = DefaultStartIntensity
                )

                Left -> horizontalGradient(
                    easing = easing,
                    startIntensity = DefaultStartIntensity,
                    endIntensity = DefaultEndIntensity
                )

                Right -> horizontalGradient(
                    easing = easing,
                    startIntensity = DefaultEndIntensity,
                    endIntensity = DefaultStartIntensity
                )
            }
        noiseFactor = 0f
    }

@Composable
internal fun fazeContainerColor() = MaterialTheme.colorScheme.scrim.copy(alpha = 0.2f)
