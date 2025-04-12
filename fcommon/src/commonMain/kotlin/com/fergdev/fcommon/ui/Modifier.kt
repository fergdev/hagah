package com.fergdev.fcommon.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

public inline fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier =
    if (condition) {
        then(Modifier.modifier())
    } else {
        this
    }

public inline fun <reified T : R, R> Modifier.conditional(
    value: R,
    modifier: Modifier.(T) -> Modifier
): Modifier =
    if (value is T) {
        then(Modifier.modifier(value))
    } else {
        this
    }

public inline fun <T> Modifier.thenLet(obj: T?, block: Modifier.(T) -> Modifier): Modifier =
    if (obj != null) then(block(Modifier, obj))
    else this

public enum class FadingEdge {
    Start, End, Top, Bottom
}

@Suppress("MagicNumber")
public fun Modifier.fadingEdge(
    fadingEdge: FadingEdge,
    size: Dp,
    rtlAware: Boolean = false,
) = composed {
    val direction = LocalLayoutDirection.current
    val invert = direction == LayoutDirection.Rtl && rtlAware
    val edge = when (fadingEdge) {
        FadingEdge.Top, FadingEdge.Bottom -> fadingEdge
        FadingEdge.Start -> if (invert) FadingEdge.End else FadingEdge.Start
        FadingEdge.End -> if (invert) FadingEdge.Start else FadingEdge.End
    }
    graphicsLayer { alpha = 0.99f }.drawWithCache {
        val colors = listOf(Color.Transparent, Color.Black)
        val sizePx = size.toPx()
        val brush = when (edge) {
            FadingEdge.Start -> Brush.horizontalGradient(colors, startX = 0f, endX = sizePx)
            FadingEdge.End -> Brush.horizontalGradient(
                colors.reversed(),
                startX = this.size.width - sizePx,
                endX = this.size.width
            )

            FadingEdge.Top -> Brush.verticalGradient(colors, startY = 0f, endY = sizePx)
            FadingEdge.Bottom -> Brush.verticalGradient(
                colors.reversed(),
                startY = this.size.height - sizePx,
                endY = this.size.height
            )
        }
        onDrawWithContent {
            drawContent()
            drawRect(
                brush = brush,
                blendMode = BlendMode.DstIn
            )
        }
    }
}

public fun Modifier.blockClicks() = this.clickable(enabled = false) { }

public fun Modifier.repeatingClickable(
    interactionSource: InteractionSource,
    enabled: Boolean = true,
    maxDelayMillis: Long = 1000,
    minDelayMillis: Long = 5,
    delayDecayFactor: Float = .20f,
    onClick: () -> Unit
): Modifier = composed {
    val currentClickListener by rememberUpdatedState(onClick)
    val coroutineScope = rememberCoroutineScope()

    pointerInput(interactionSource, enabled) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val heldButtonJob = coroutineScope.launch {
                var currentDelayMillis = maxDelayMillis
                while (enabled && down.pressed) {
                    currentClickListener()
                    delay(currentDelayMillis)
                    val nextMillis =
                        currentDelayMillis - currentDelayMillis * delayDecayFactor
                    currentDelayMillis = nextMillis.toLong().coerceAtLeast(minDelayMillis)
                }
            }
            waitForUpOrCancellation()
            heldButtonJob.cancel()
        }
    }
}
