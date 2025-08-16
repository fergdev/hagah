package com.fergdev.hagah.data

import com.fergdev.hagah.Flavor
import com.fergdev.hagah.data.api.DailyDevotionalApi
import com.fergdev.hagah.data.api.DailyDevotionalApiImpl
import com.fergdev.hagah.data.api.httpClient
import com.fergdev.hagah.data.storage.HagahStorage
import com.fergdev.hagah.data.storage.HagahStorageImpl
import com.fergdev.hagah.data.video.VideoApi
import com.fergdev.hagah.data.video.VideoApiImpl
import com.fergdev.hagah.data.video.VideoRepository
import com.fergdev.hagah.data.video.VideoRepositoryImpl
import com.fergdev.hagah.data.video.VideoStorage
import com.fergdev.hagah.data.video.VideoStorageImpl
import com.fergdev.hagah.enabled
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val dataModule = module {
    single<HttpClient> { httpClient() }.bind<HttpClient>()
    singleOf(::DailyDevotionalApiImpl).bind<DailyDevotionalApi>()
    singleOf(::HagahStorageImpl).bind<HagahStorage>()
    if (Flavor.Mock.enabled) {
        singleOf(::DataRepositoryMockImpl).bind<DataRepository>()
    } else {
        singleOf(::DataRepositoryImpl).bind<DataRepository>()
    }
    singleOf(::VideoRepositoryImpl).bind<VideoRepository>()
    singleOf(::VideoApiImpl).bind<VideoApi>()
    singleOf(::VideoStorageImpl).bind<VideoStorage>()
}
