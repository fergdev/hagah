package com.fergdev.hagah.di

import android.content.Context
import com.fergdev.hagah.data.HagahDirectories
import com.fergdev.hagah.data.video.VideoFileSystem
import com.fergdev.hagah.data.video.VideoFileSystemAndroid
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        val context = get<Context>()
        object : HagahDirectories {
            override fun dataDir(): String {
                val dir = context.cacheDir.resolve(HagahDirectories.dataDir)
                dir.mkdirs()
                return dir.absolutePath
            }

            override fun videoCacheDir(): String {
                val dir = context.cacheDir.resolve(HagahDirectories.VideoDir)
                dir.mkdirs()
                return dir.absolutePath
            }
        }
    }.bind<HagahDirectories>()
    singleOf(::VideoFileSystemAndroid).bind<VideoFileSystem>()
}
