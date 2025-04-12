package com.fergdev.fcommon.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
public fun IButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current,
    contentDescription: String? = null,
): Unit = IconButton(
    modifier = modifier,
    enabled = enabled,
    onClick = onClick
) {
    Icon(
        imageVector = imageVector,
        tint = tint,
        contentDescription = contentDescription
    )
}
