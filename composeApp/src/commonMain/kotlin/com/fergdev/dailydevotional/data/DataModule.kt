package com.fergdev.dailydevotional.data

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private class KtorLogger : Logger {
    override fun log(message: String) {
        Napier.e(message = message, tag = "Ktor")
    }
}

private const val HTTP_TIMEOUT = 30_000L
val dataModule = module {
    single {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        HttpClient {
            install(ContentNegotiation) {
                json(json = json, contentType = ContentType.Any)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = HTTP_TIMEOUT
                socketTimeoutMillis = HTTP_TIMEOUT
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = KtorLogger()
            }
        }
    }

    single<DailyDevotionalApi> { DailyDevotionalApiImpl(get()) }
    single<DailyDevotionalStorage> { InMemoryStorage() }
    single { DailyDevotionalRepository() }
}
