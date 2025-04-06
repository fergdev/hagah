@file:Suppress("Filename")
package com.fergdev.dailydevotional

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fergdev.dailydevotional.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject",
    ) {
        App()
    }
}
