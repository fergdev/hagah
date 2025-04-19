package com.fergdev.hagah.di

import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.HagahDirectories
import com.fergdev.hagah.data.storage.HagahDb
import com.fergdev.hagah.data.storage.HagahStoreWrapper
import com.fergdev.hagah.data.video.VideoAssetsDb
import com.fergdev.hagah.data.video.VideoDownloader
import com.fergdev.hagah.data.video.VideoDownloaderImpl
import com.fergdev.hagah.data.video.VideoFileSystem
import com.fergdev.hagah.data.video.VideoFileSystemWasm
import com.fergdev.hagah.data.video.VideoInfo
import com.fergdev.hagah.data.video.VideoStoreWrapper
import io.github.xxfast.kstore.storage.storeOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        HagahStoreWrapper(
            storeOf<List<DailyHagah>>(key = HagahDb, default = emptyList())
        )
    }.bind<HagahStoreWrapper>()
    single {
        VideoStoreWrapper(
            storeOf<List<VideoInfo.CachedVideoInfo>>(key = VideoAssetsDb, default = emptyList())
        )
    }.bind<VideoStoreWrapper>()
    singleOf(::VideoDownloaderImpl).bind<VideoDownloader>()
    singleOf(::VideoFileSystemWasm).bind<VideoFileSystem>()
    singleOf(::HagahDirectoriesWasm).bind<HagahDirectories>()
}

class HagahDirectoriesWasm : HagahDirectories {
    override fun dataDir() = ""
    override fun videoCacheDir() = ""
}
