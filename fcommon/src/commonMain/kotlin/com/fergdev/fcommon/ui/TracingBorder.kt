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
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun AnimatedTracingBorder(
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    strokeWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "border-trace")
    val animatedProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "path-trace"
    )

    TracingBorder(
        modifier = modifier,
        enabled = enabled,
        strokeWidth = strokeWidth,
        cornerRadius = cornerRadius,
        progress = { animatedProgress },
        content = content
    )
}

@Composable
public fun TracingBorder(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    strokeWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    progress: () -> Float,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(modifier = modifier.clip(shape)) {
        if (enabled) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .padding(strokeWidth / 2)
            ) {
                val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                val outline = shape.createOutline(size, layoutDirection, this)
                val fullPath = Path().apply {
                    when (outline) {
                        is Outline.Rounded -> addRoundRect(outline.roundRect)
                        else -> addRect(Rect(Offset.Zero, size))
                    }
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(fullPath, false)

                val tracePath = Path()
                val length = pathMeasure.length
                val start = 0f
                val end = progress().coerceIn(0f, 1f) * length

                pathMeasure.getSegment(start, end, tracePath, true)

                drawPath(tracePath, Color.White, style = stroke)
            }
        }
        Box(modifier = Modifier.padding(strokeWidth)) {
            content()
        }
    }
}
