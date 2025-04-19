package com.fergdev.hagah.screens.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import hagah.generated.resources.Cinzel_Regular
import hagah.generated.resources.Lora_Regular
import hagah.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
private fun displayFontFamily() = FontFamily(Font(Res.font.Cinzel_Regular))

@Composable
private fun bodyFontFamily() = FontFamily(Font(Res.font.Lora_Regular))
private val baseline = Typography()

@Composable
fun typog(): Typography {
    val displayFontFamily = displayFontFamily()
    val bodyFontFamily = bodyFontFamily()
    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = bodyFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = bodyFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = bodyFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    )
}
