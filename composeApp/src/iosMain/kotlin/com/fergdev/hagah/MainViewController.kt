package com.fergdev.hagah

import androidx.compose.ui.window.ComposeUIViewController
import com.fergdev.hagah.data.DailyDevotional
import com.fergdev.hagah.data.storage.HagahDb
import com.fergdev.hagah.di.startKoin
import io.github.xxfast.kstore.file.FileCodec
import io.github.xxfast.kstore.storeOf
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private val desktopModule = module {
    single {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )!!.path!!

        val file = Path(documentDirectory, HagahDb)
        storeOf<List<DailyDevotional>>(
            codec = FileCodec(file),
            default = emptyList()
        )
    }
}

@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    startKoin(modules = listOf(desktopModule))
    App()
}
