@file:Suppress("Filename")

package com.fergdev.hagah

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.di.startKoin
import io.github.kdroidfilter.composemediaplayer.htmlinterop.LocalLayerContainer
import io.github.xxfast.kstore.storage.storeOf
import kotlinx.browser.document
import org.koin.dsl.module

val wasmModule = module {
    single {
        storeOf<List<DailyHagah>>(key = "dailyDevotional", default = emptyList())
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(
        canvasElementId = "ComposeTarget",
        title = BuildFlags.appName,
    ) {
        startKoin(modules = listOf(wasmModule))
        CompositionLocalProvider(LocalLayerContainer provides document.body!!) {
            App()
        }
    }
}

external fun appLoaded()
