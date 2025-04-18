package com.fergdev.fcommon.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun PulsingBorderCard(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = Color.White,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "border-pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-alpha"
    )

    Box(modifier = modifier.clip(shape = RoundedCornerShape(cornerRadius))) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .padding(strokeWidth / 2)
        ) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val shape = RoundedCornerShape(cornerRadius)
            val outline = shape.createOutline(size, layoutDirection, this)
            val fullPath = Path().apply {
                when (outline) {
                    is Outline.Rounded -> addRoundRect(outline.roundRect)
                    else -> addRect(Rect(Offset.Zero, size))
                }
            }
            drawPath(fullPath, borderColor.copy(alpha = alpha), style = stroke)
        }

        Box(modifier = Modifier.padding(strokeWidth)) {
            content()
        }
    }
}
