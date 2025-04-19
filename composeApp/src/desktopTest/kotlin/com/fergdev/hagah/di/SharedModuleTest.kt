package com.fergdev.hagah.di

import com.fergdev.hagah.data.api.DailyDevotionalApi
import com.fergdev.hagah.data.api.DailyDevotionalApiImpl
import com.fergdev.hagah.data.storage.HagahStoreWrapper
import com.fergdev.hagah.data.video.VideoDownloader
import com.fergdev.hagah.data.video.VideoStoreWrapper
import io.github.xxfast.kstore.KStore
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import kotlinx.datetime.Clock
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class SharedModuleTest : FreeSpec({
    "shared module verify" {
        sharedModule.verify(
            injections = injectedParameters(
                definition<Clock>(Clock::class),
                definition<HttpClient>(),
                definition<DailyDevotionalApi>(DailyDevotionalApiImpl::class),
            ),
            extraTypes = listOf(
                HttpClientConfig::class,
                HttpClientEngine::class,
                KStore::class,
                HagahStoreWrapper::class,
                VideoStoreWrapper::class,
                VideoDownloader::class
            )
        )
    }
    "get clock" {
        org.koin.core.context.startKoin {
            modules(sharedModule)
        }
        KoinPlatform.getKoin().get<Clock>().shouldNotBeNull()
        stopKoin()
    }
})
