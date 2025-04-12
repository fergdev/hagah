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

private class KtorLogger : Logger {
    override fun log(message: String) {
        Napier.v(message = message, tag = "Ktor")
    }
}

private const val HTTP_TIMEOUT = 60_000L
private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
internal val httpClient: HttpClient = HttpClient {
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
