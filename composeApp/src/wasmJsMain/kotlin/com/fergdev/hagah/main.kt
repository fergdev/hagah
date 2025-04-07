@file:Suppress("Filename")

package com.fergdev.hagah

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.fergdev.hagah.data.DailyDevotional
import com.fergdev.hagah.di.startKoin
import io.github.xxfast.kstore.storage.storeOf
import kotlinx.browser.document
import org.koin.dsl.module

val wasmModule = module {
    single {
        storeOf<List<DailyDevotional>>(key = "dailyDevotional", default = emptyList())
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        startKoin(modules = listOf(wasmModule))
        App()
    }
}
