package com.fergdev.haga.data.video

import arrow.core.Either
import com.fergdev.hagah.data.HagahDirectories
import com.fergdev.hagah.data.video.VideoApi
import com.fergdev.hagah.data.video.VideoDownloader
import com.fergdev.hagah.data.video.VideoFileSystem
import com.fergdev.hagah.data.video.VideoManifestAsset
import com.fergdev.hagah.data.video.videoHttpClient
import com.fergdev.hagah.logger
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import kotlinx.io.files.Path

internal class VideoDownloaderImpl(
    private val hagahDirectories: HagahDirectories,
    private val videoFileSystem: VideoFileSystem
) : VideoDownloader {
    private val client = videoHttpClient()
    private val logger = logger("VideoDownloader")
    override suspend fun downloadVideo(
        videoManifest: VideoManifestAsset
    ) = Either.catch {
        logger.d { "Downloading video: ${videoManifest.url}" }
        val videoDir = hagahDirectories.videoCacheDir()
        val storagePath = Path(videoDir, videoManifest.filename).toString()
        val get = client.get(videoManifest.url)
        videoFileSystem.writeStreamToFile(get.bodyAsChannel(), storagePath) { downloadedBytes ->
            // Maybe handle progress
        }
        storagePath
    }.mapLeft {
        logger.e("Error downloading video", it)
        VideoApi.Error.Network
    }
}
