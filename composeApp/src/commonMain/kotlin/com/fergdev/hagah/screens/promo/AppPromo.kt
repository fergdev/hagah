package com.fergdev.hagah.screens.promo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.TypewriteText
import com.fergdev.hagah.ui.HCard

private val HagahIntro = """
    Hagah is a Hebrew word meaning to meditate or reflect deeply.
    It’s about quieting the mind to connect with God’s Word,
    allowing it to shape and bring peace to your heart.
""".trimIndent()

@Composable
fun AppPromoContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        HCard(modifier = Modifier.wrapContentSize()) {
            TypewriteText(
                modifier = Modifier.padding(16.dp),
                text = HagahIntro,
                repeat = true,
            )
        }
    }
}
