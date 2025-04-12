package com.fergdev.fcommon.ui.widgets

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.abs
import com.fergdev.fcommon.ui.widgets.Screen.DOWN
import com.fergdev.fcommon.ui.widgets.Screen.LEFT
import com.fergdev.fcommon.ui.widgets.Screen.MAIN
import com.fergdev.fcommon.ui.widgets.Screen.RIGHT
import com.fergdev.fcommon.ui.widgets.Screen.UP
import kotlin.math.roundToInt

public enum class Screen {
    MAIN, UP, DOWN, LEFT, RIGHT
}

public fun Screen.canHorizontal(): Boolean = when (this) {
    MAIN, LEFT, RIGHT -> true
    else -> false
}

public fun Screen.canVertical(): Boolean = when (this) {
    MAIN, UP, DOWN -> true
    else -> false
}

public data class FiveWaySwipeableScreenScope(val navigate: (Screen) -> Unit)

@Suppress("CyclomaticComplexMethod")
@Composable
public fun FiveWaySwipeableScreen(
    modifier: Modifier = Modifier,
    screens: @Composable FiveWaySwipeableScreenScope.(Screen) -> Unit
) {
    val dragThreshold = remember { 40.dp }
    var currentScreen by remember { mutableStateOf(MAIN) }
    var startX by remember { mutableStateOf(0.dp) }
    var startY by remember { mutableStateOf(0.dp) }

    var lastXChange by remember { mutableStateOf(0.dp) }
    var lastYChange by remember { mutableStateOf(0.dp) }
    var x by remember { mutableStateOf(0.dp) }
    var y by remember { mutableStateOf(0.dp) }

    val animX by animateDpAsState(x)
    val animY by animateDpAsState(y)

    var height by remember { mutableStateOf(1000.dp) }
    var width by remember { mutableStateOf(1000.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier.onGloballyPositioned {
            with(density) {
                height = it.size.height.toDp()
                width = it.size.width.toDp()
            }
        }.fillMaxSize().pointerInput(Unit) {
            detectVerticalDragGestures(onDragEnd = {
                val diff = y - startY
                if (diff.abs() < dragThreshold) {
                    y = startY
                    return@detectVerticalDragGestures
                }
                when (currentScreen) {
                    MAIN -> {
                        if (diff > 0.dp && lastYChange > 0.dp) {
                            y = startY + height
                            currentScreen = UP
                        } else if (diff < 0.dp && lastYChange < 0.dp) {
                            y = startY - height
                            currentScreen = DOWN
                        } else {
                            y = startY
                        }
                    }

                    UP -> {
                        if (lastYChange < 0.dp) {
                            y = 0.dp
                            currentScreen = MAIN
                        } else {
                            y = startY
                        }
                    }

                    DOWN -> {
                        if (lastYChange > 0.dp) {
                            y = 0.dp
                            currentScreen = MAIN
                        } else {
                            y = startY
                        }
                    }

                    else -> {
                        // Noop
                    }
                }
                startY = y
            }) { change, dragAmount ->
                with(density) {
                    if (currentScreen.canVertical()) {
                        change.consume()
                        val yChangeDp = dragAmount.roundToInt().toDp()
                        lastYChange = yChangeDp
                        y += yChangeDp
                    }
                }
            }
        }.pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    val diff = x - startX
                    if (diff.abs() < dragThreshold) {
                        x = startX
                        return@detectHorizontalDragGestures
                    }
                    when (currentScreen) {
                        MAIN -> {
                            if (diff > 0.dp && lastXChange > 0.dp) {
                                x = startX + width
                                currentScreen = LEFT
                            } else if (diff < 0.dp && lastXChange < 0.dp) {
                                x = startX - width
                                currentScreen = RIGHT
                            } else {
                                x = startX
                            }
                        }

                        LEFT -> {
                            if (lastXChange < 0.dp && diff < 0.dp) {
                                x = 0.dp
                                currentScreen = MAIN
                            } else {
                                x = startX
                            }
                        }

                        RIGHT -> {
                            if (lastXChange > 0.dp && diff > 0.dp) {
                                x = 0.dp
                                currentScreen = MAIN
                            } else {
                                x = startX
                            }
                        }

                        else -> {
                            // We don't scroll
                        }
                    }
                    startX = x
                }
            ) { change, dragAmount ->
                if (currentScreen.canHorizontal()) {
                    change.consume()
                    with(density) {
                        val xChangeDp = dragAmount.roundToInt().toDp()
                        lastXChange = xChangeDp
                        x += xChangeDp
                    }
                }
            }
        }
    ) {
        FiveWaySwipeableScreenScope {
            when (it) {
                MAIN -> {
                    x = 0.dp
                    y = 0.dp
                }

                UP -> {
                    x = 0.dp
                    y = -height
                }

                DOWN -> {
                    x = 0.dp
                    y = height
                }

                LEFT -> {
                    x = -width
                    y = 0.dp
                }

                RIGHT -> {
                    x = width
                    y = 0.dp
                }
            }
            startX = x
            startY = y
            currentScreen = it
        }.apply {
            Box(
                modifier = Modifier.fillMaxSize().offset(animX, animY),
                contentAlignment = Alignment.Center
            ) {
                screens(MAIN)
            }
            Box(
                modifier = Modifier.fillMaxSize().offset(0.dp, animY - height),
                contentAlignment = Alignment.Center
            ) {
                screens(UP)
            }
            Box(
                modifier = Modifier.fillMaxSize().offset(0.dp, animY + height),
                contentAlignment = Alignment.Center
            ) {
                screens(DOWN)
            }
            Box(
                modifier = Modifier.fillMaxSize().offset(animX - width, 0.dp),
                contentAlignment = Alignment.Center
            ) {
                screens(LEFT)
            }
            Box(
                modifier = Modifier.fillMaxSize().offset(animX + width, 0.dp),
                contentAlignment = Alignment.Center
            ) {
                screens(RIGHT)
            }
        }
    }
}
