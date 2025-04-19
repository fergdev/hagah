package com.fergdev.fcommon.ui.widgets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
public fun PulsingIcon(
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    val pulseTransition = rememberInfiniteTransition(label = "download-pulse")
    val scale by pulseTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700, easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "download-icon-scale"
    )
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable { onClick() }
    )
}
