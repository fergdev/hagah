package com.fergdev.fcommon.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
public fun TypewriteText(
    text: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    repeat: Boolean = false,
    spec: DurationBasedAnimationSpec<Int> = tween(
        durationMillis = text.length * 100,
        easing = LinearEasing
    ),
    style: TextStyle = LocalTextStyle.current,
    textColor: Color = LocalContentColor.current,
    preoccupySpace: Boolean = true,
    onTypingFinish: () -> Unit = {},
) {
    val spec = remember(repeat, spec) {
        if (repeat) {
            infiniteRepeatable(animation = spec)
        } else {
            spec
        }
    }
    // State that keeps the text that is currently animated
    var textToAnimate by remember { mutableStateOf("") }

    // Animatable index to control the progress of the animation
    val index = remember {
        Animatable(initialValue = 0, typeConverter = Int.VectorConverter)
    }
    // Effect to handle the animation's completion
    LaunchedEffect(index.value) {
        if (index.value == text.length) {
            // Execute the callback when the animation is complete
            onTypingFinish()
        }
    }

    // Effect to handle animation when visibility changes
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Start animation if visible
            textToAnimate = text
            index.animateTo(targetValue = text.length, animationSpec = spec)
        } else {
            // Snap to the beginning if not visible
            index.snapTo(targetValue = 0)
        }
    }

    // Effect to handle animation when text content changes
    LaunchedEffect(text) {
        if (isVisible) {
            // Reset animation and update text if visible
            index.snapTo(0)
            textToAnimate = text
            index.animateTo(text.length, spec)
        }
    }

    // Box composable to contain the animated and static text
    Box(modifier = modifier) {
        if (preoccupySpace && index.isRunning) {
            // Display invisible text when preoccupation is turned on
            // and the animation is in progress.
            // Plays the role of a placeholder to occupy the space
            // that will be filled with text.
            Text(
                text = text,
                style = style,
                modifier = Modifier.alpha(0f)
            )
        }

        Text(
            text = textToAnimate.substring(0, index.value),
            style = style,
            color = textColor
        )
    }
}
