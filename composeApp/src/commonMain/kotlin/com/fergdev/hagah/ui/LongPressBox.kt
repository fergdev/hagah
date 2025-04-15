package com.fergdev.hagah.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.log
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun LongPressBox(
    indicatorSize: Dp = 100.dp,
    onAction: () -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var showIndicator by remember { mutableStateOf(false) }
    var indicatorPosition by remember { mutableStateOf(Offset.Companion.Zero) }
    val indicatorScale = remember { Animatable(0f) }
    val density = LocalDensity.current
    val indicatorSizeInPx = with(density) { indicatorSize.toPx() }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Companion.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    showIndicator = false
                }, onDoubleTap = { offset ->
                    log(tag = "LongPressBox") { "onDoubleTap $offset" }
                    indicatorPosition = offset
                    coroutineScope.launch {
                        showIndicator = true
                        indicatorScale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = 0.35f, stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                })
            },
    ) {
        content()
        if (showIndicator) {
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = indicatorScale.value
                    scaleY = indicatorScale.value
                    alpha = min(1f, indicatorScale.value)
                    translationX = indicatorPosition.x - indicatorSizeInPx / 2
                    translationY = indicatorPosition.y - indicatorSizeInPx / 2
                }
            ) {
                Box(
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            indicatorScale.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = 0.35f, stiffness = Spring.StiffnessMediumLow
                                )
                            )
                            showIndicator = false
                        }
                        onAction()
                    }
                ) {
                    Text(modifier = Modifier.padding(16.dp), text = "Share")
                }
            }
        }
    }
}
