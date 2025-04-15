package com.fergdev.hagah.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.FConstants.Opacity

@Composable
fun containerColor(): Color =
    MaterialTheme.colorScheme.scrim.copy(alpha = Opacity.TINT)

@Composable
fun HCard(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.clip(shape)) {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = containerColor(),
                contentColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment
            ) {
                content()
            }
        }
    }
}
