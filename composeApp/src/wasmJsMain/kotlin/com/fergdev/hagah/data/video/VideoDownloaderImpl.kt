package com.fergdev.hagah.data.video

import arrow.core.Either
import arrow.core.right

internal class VideoDownloaderImpl : VideoDownloader {
    override suspend fun downloadVideo(videoManifest: VideoManifestAsset):
        Either<VideoApi.Error, String> = videoManifest.url.right()
}
