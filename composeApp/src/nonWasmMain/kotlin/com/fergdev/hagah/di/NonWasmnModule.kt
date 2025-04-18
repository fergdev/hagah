package com.fergdev.hagah.di

import com.fergdev.haga.data.video.VideoDownloaderImpl
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.HagahDirectories
import com.fergdev.hagah.data.storage.HagahDb
import com.fergdev.hagah.data.storage.HagahStoreWrapper
import com.fergdev.hagah.data.video.VideoAssetsDb
import com.fergdev.hagah.data.video.VideoDownloader
import com.fergdev.hagah.data.video.VideoInfo
import com.fergdev.hagah.data.video.VideoStoreWrapper
import io.github.xxfast.kstore.file.FileCodec
import io.github.xxfast.kstore.storeOf
import kotlinx.io.files.Path
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val nonWasmModule = module {
    singleOf(::VideoDownloaderImpl).bind<VideoDownloader>()
    single {
        val hagahDirectories = get<HagahDirectories>()
        HagahStoreWrapper(
            storeOf<List<DailyHagah>>(
                codec = FileCodec(Path(hagahDirectories.dataDir(), HagahDb)),
                default = emptyList()
            )
        )
    }
    single {
        val hagahDirectories = get<HagahDirectories>()
        VideoStoreWrapper(
            storeOf<List<VideoInfo.CachedVideoInfo>>(
                codec = FileCodec(Path(hagahDirectories.dataDir(), VideoAssetsDb)),
                default = emptyList()
            )
        )
    }
}
