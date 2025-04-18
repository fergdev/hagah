package com.fergdev.fcommon.ui.effects

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

public fun Modifier.animatedGradientBorder(
    enabled: Boolean = true,
    colors: List<Color> = listOf(Color.White, Color.Black, Color.White),
): Modifier = composed {
    if (enabled) {
        val transition = rememberInfiniteTransition(label = "animated-gradient")
        val offset by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "gradient-offset"
        )

        val gradientBrush = Brush.linearGradient(
            colors = colors,
            start = Offset(0f, 0f),
            end = Offset(offset, offset)
        )

        this.border(
            width = 2.dp,
            brush = gradientBrush,
            shape = RoundedCornerShape(16.dp)
        )
    } else {
        this
    }
}
