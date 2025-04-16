@file:Suppress("Filename")

package com.fergdev.hagah

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ca.gosyer.appdirs.AppDirs
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.storage.HagahDb
import com.fergdev.hagah.di.startKoin
import io.github.aakira.napier.log
import io.github.xxfast.kstore.file.FileCodec
import io.github.xxfast.kstore.storeOf
import kotlinx.io.files.Path
import org.koin.dsl.module

private val desktopModule = module {
    single {
        val filesDir = AppDirs(
            BuildFlags.appName,
            BuildFlags.versionName
        )

        val file = Path(filesDir.getUserDataDir(), HagahDb)
        log { "DesktopModule dbFile: $file" }
        storeOf<List<DailyHagah>>(
            codec = FileCodec(file),
            default = emptyList()
        )
    }
}

fun main() = application {
    startKoin(modules = listOf(desktopModule))
    Window(
        onCloseRequest = ::exitApplication,
        undecorated = true,
        resizable = true,
        state = WindowState(size = DpSize(800.dp, 600.dp)),
        transparent = false, // set to true if you want a transparent background
        title = BuildFlags.appName,
    ) {
        App()
    }
}
