@file:Suppress("Filename")

package com.fergdev.hagah

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fergdev.hagah.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject",
    ) {
        App()
    }
}
