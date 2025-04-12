package com.fergdev.fcommon.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun Spacer(modifier: Modifier = Modifier, width: Dp = 0.dp, height: Dp = 0.dp): Unit =
    androidx.compose.foundation.layout.Spacer(
        modifier = modifier
            .conditional(width != 0.dp) { width(width) }
            .conditional(height != 0.dp) { height(height) }
    )
