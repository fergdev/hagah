package com.fergdev.hagah.data.api

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

private const val HTTP_TIMEOUT = 60_000L
internal fun httpClient(): HttpClient = HttpClient {
    expectSuccess = true
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
    install(HttpTimeout) {
        requestTimeoutMillis = HTTP_TIMEOUT
        socketTimeoutMillis = HTTP_TIMEOUT
        connectTimeoutMillis = HTTP_TIMEOUT
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                Napier.v(message = message, tag = "Ktor")
            }
        }
    }
}
