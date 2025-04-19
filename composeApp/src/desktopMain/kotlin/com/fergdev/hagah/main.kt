@file:Suppress("Filename")

package com.fergdev.hagah

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.fergdev.hagah.di.nonWasmModule
import com.fergdev.hagah.di.startKoin

fun main() = application {
    startKoin(modules = listOf(nonWasmModule))
    Window(
        onCloseRequest = ::exitApplication,
        resizable = true,
        state = WindowState(size = DpSize(800.dp, 600.dp)),
        title = BuildFlags.appName,
    ) {
        App()
    }
}
