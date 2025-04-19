package com.fergdev.hagah.server.di

import com.fergdev.hagah.server.api.ChatGPTApi
import com.fergdev.hagah.server.database.DatabaseFactory
import com.fergdev.hagah.server.repository.DevotionalRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.ktorm.database.Database

val serverModule = DI.Module("devotionalModule") {
    bind<ChatGPTApi>() with singleton { ChatGPTApi(instance()) }
    bind<HttpClient>() with singleton {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    },
                    contentType = ContentType.Application.Json
                )
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
    bind<DevotionalRepository>() with singleton { DevotionalRepository(instance()) }
    bind<Database>() with singleton { DatabaseFactory.init() }
}
