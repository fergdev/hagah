package com.fergdev.hagah.di

import com.fergdev.hagah.data.HagahDirectories
import com.fergdev.hagah.data.video.VideoFileSystem
import com.fergdev.hagah.data.video.VideoFileSystemDesktop
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

actual val platformModule: Module = module {
    single {
        object : HagahDirectories {
            override fun dataDir(): String {
                val dir = File(System.getProperty("java.io.tmpdir"))
                    .resolve(HagahDirectories.dataDir)
                dir.mkdirs()
                return dir.absolutePath
            }

            override fun videoCacheDir(): String {
                val dir = File(System.getProperty("java.io.tmpdir"))
                    .resolve(HagahDirectories.VideoDir)
                dir.mkdirs()
                return dir.absolutePath
            }
        }
    }.bind<HagahDirectories>()
    singleOf(::VideoFileSystemDesktop).bind<VideoFileSystem>()
}
