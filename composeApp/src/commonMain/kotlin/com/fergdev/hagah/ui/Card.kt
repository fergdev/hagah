package com.fergdev.hagah.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Opacity {
    const val enabled = 1f
    const val secondary = 0.85f
    const val semiTransparent = 0.66f
    const val scrim = 0.5f
    const val disabled = 0.38f
    const val tint = 0.2f
}

@Composable
fun containerColor(): Color =
    MaterialTheme.colorScheme.surface.copy(alpha = Opacity.secondary)

@Composable
fun TCard(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    internalPadding: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(containerColor())
            .thenLet(onClick) { clickable(onClick = it) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(internalPadding),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            content()
        }
    }
}
inline fun <T> Modifier.thenLet(obj: T?, block: Modifier.(T) -> Modifier): Modifier =
    if (obj != null) then(block(Modifier, obj))
    else this
