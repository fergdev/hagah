package com.fergdev.hagah.data

import com.fergdev.hagah.BuildFlags
import com.fergdev.hagah.data.api.DailyDevotionalApi
import com.fergdev.hagah.data.api.DailyDevotionalApiImpl
import com.fergdev.hagah.data.api.httpClient
import com.fergdev.hagah.data.storage.DailyDevotionalStorage
import com.fergdev.hagah.data.storage.DailyDevotionalStorageImpl
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single<HttpClient> { httpClient }.bind<HttpClient>()
    singleOf(::DailyDevotionalApiImpl).bind<DailyDevotionalApi>()
    singleOf(::DailyDevotionalStorageImpl).bind<DailyDevotionalStorage>()
    if (BuildFlags.mockData.isBlank()) {
        singleOf(::DataRepositoryImpl).bind<DataRepository>()
    } else {
        singleOf(::DataRepositoryMockImpl).bind<DataRepository>()
    }
    singleOf(::VideoRepositoryMock).bind<VideoRepository>()
}
