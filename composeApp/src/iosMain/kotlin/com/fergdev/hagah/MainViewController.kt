package com.fergdev.hagah

import androidx.compose.ui.window.ComposeUIViewController
import com.fergdev.hagah.di.nonWasmModule
import com.fergdev.hagah.di.startKoin

@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    startKoin(modules = listOf(nonWasmModule))
    App()
}
