package com.fergdev.hagah.data.video

import arrow.core.Either
import com.fergdev.hagah.logger
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class VideoManifest(
    val version: String,
    val videos: List<VideoManifestAsset>
)

@Serializable
internal data class VideoManifestAsset(
    val filename: String,
    val title: String,
    val url: String,
    val size: Long
)

internal interface VideoApi {
    interface Error {
        data object Network : Error
    }

    suspend fun fetchManifest(): Either<Error, VideoManifest>
}

internal fun videoHttpClient(): HttpClient = HttpClient {
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
    install(Logging) {
        level = LogLevel.HEADERS
        logger = object : Logger {
            override fun log(message: String) {
                Napier.v(message = message, tag = "VideoHttpClient")
            }
        }
    }
}

internal class VideoApiImpl : VideoApi {
    private val client = videoHttpClient()
    private val logger = logger("VideoApi")
    private val rootUrl = "https://github.com/fergdev/hagah/releases/download/v1.0.0/"
    private val manifestUrl = "$rootUrl/manifest.json"

    override suspend fun fetchManifest() = Either.catch {
        Json.decodeFromString<VideoManifest>(
            client.get(manifestUrl).bodyAsBytes().decodeToString()
        )
    }.onRight { logger.list(it.videos) { "Manifest videos" } }
        .mapLeft {
            logger.e("Error fetching manifest", it)
            VideoApi.Error.Network
        }
}

internal interface VideoDownloader {
    suspend fun downloadVideo(videoManifest: VideoManifestAsset): Either<VideoApi.Error, String>
}
