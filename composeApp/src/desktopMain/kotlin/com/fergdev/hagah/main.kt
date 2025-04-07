@file:Suppress("Filename")

package com.fergdev.hagah

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ca.gosyer.appdirs.AppDirs
import com.fergdev.hagah.data.DailyDevotional
import com.fergdev.hagah.data.storage.HagahDb
import com.fergdev.hagah.di.startKoin
import io.github.xxfast.kstore.file.FileCodec
import io.github.xxfast.kstore.storeOf
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.koin.dsl.module

private val desktopModule = module {
    single {
        val filesDir = AppDirs(
            BuildFlags.appName,
            BuildFlags.versionName
        )

        val file = Path(filesDir.getUserDataDir(), HagahDb)
        SystemFileSystem.delete(file)
        storeOf<List<DailyDevotional>>(
            codec = FileCodec(file),
            default = emptyList()
        )
    }
}

fun main() = application {
    startKoin(modules = listOf(desktopModule))
    Window(
        onCloseRequest = ::exitApplication,
        title = BuildFlags.appName,
    ) {
        App()
    }
}
