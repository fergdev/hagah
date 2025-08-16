package com.fergdev.fcommon.ui.text

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle

public fun TextStyle.italic() : TextStyle = this.copy(fontStyle = FontStyle.Italic)
