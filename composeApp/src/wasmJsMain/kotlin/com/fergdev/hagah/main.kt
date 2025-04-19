@file:Suppress("Filename")

package com.fergdev.hagah

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.fergdev.hagah.di.startKoin
import io.github.kdroidfilter.composemediaplayer.htmlinterop.LocalLayerContainer
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(
        canvasElementId = "ComposeTarget",
        title = BuildFlags.appName,
    ) {
        startKoin()
        CompositionLocalProvider(LocalLayerContainer provides document.body!!) {
            App()
        }
    }
}
