package com.fergdev.hagah.di

import com.fergdev.hagah.data.HagahDirectories
import com.fergdev.hagah.data.video.VideoFileSystem
import com.fergdev.hagah.data.video.VideoFileSystemIos
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module = module {
    singleOf(::VideoFileSystemIos).bind<VideoFileSystem>()
    single {
        object : HagahDirectories {
            override fun dataDir() =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )!!.path!!

            override fun videoCacheDir(): String {
                val cacheUrl = NSFileManager.defaultManager
                    .URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
                    .first() as NSURL
                return cacheUrl.path!!
            }
        }
    }.bind<HagahDirectories>()
}
