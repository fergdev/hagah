package com.fergdev.hagah.data.video

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fergdev.hagah.Flavor
import com.fergdev.hagah.clean
import com.fergdev.hagah.data.video.VideoInfo.CachedVideoInfo
import com.fergdev.hagah.data.video.VideoInfo.DownloadableVideoInfo
import com.fergdev.hagah.data.video.VideoRepository.DownloadError.Network
import com.fergdev.hagah.data.video.VideoRepository.DownloadError.NotFound
import com.fergdev.hagah.logger
import com.russhwolf.settings.coroutines.FlowSettings
import hagah.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import kotlin.coroutines.EmptyCoroutineContext

internal interface VideoRepository {
    val playingVideo: Flow<Either<NotFound, VideoInfo.PlayableVideoInfo>>
    val allVideos: StateFlow<List<VideoInfo>>
    suspend fun playVideo(id: String): Either<NotFound, Unit>
    suspend fun downloadVideo(id: String): Either<DownloadError, String>
    suspend fun deleteVideo(id: String): Either<NotFound, Unit>

    interface DownloadError {
        object Network : DownloadError
        object NotFound : DownloadError
    }
}

internal sealed interface VideoInfo {
    val videoManifest: VideoManifestAsset

    fun id(): String = videoManifest.filename

    sealed interface PlayableVideoInfo : VideoInfo {
        fun playPath(): String
    }

    data class AppVideoInfo(override val videoManifest: VideoManifestAsset) : PlayableVideoInfo {
        override fun playPath(): String = videoManifest.url
    }

    @Serializable
    data class CachedVideoInfo(
        override val videoManifest: VideoManifestAsset,
        val storedPath: String
    ) : PlayableVideoInfo {
        override fun playPath(): String = storedPath
    }

    data class DownloadableVideoInfo(override val videoManifest: VideoManifestAsset) : VideoInfo
}

private const val KeyPrefix = "VideoRepository"
private const val KeyCurrentVideo = "$KeyPrefix:currentVideo"

internal class VideoRepositoryImpl(
    private val settings: FlowSettings,
    private val videoApi: VideoApi,
    private val videoDownloader: VideoDownloader,
    private val videoStorage: VideoStorage,
) : VideoRepository {
    private val logger = logger("VideoRepository")
    private val scope = CoroutineScope(EmptyCoroutineContext)

    @OptIn(ExperimentalResourceApi::class)
    private val appVideos: List<VideoInfo.AppVideoInfo> = listOf(
        VideoInfo.AppVideoInfo(
            videoManifest = VideoManifestAsset(
                filename = "1918465-uhd_2560_1440_24fps.mp4",
                title = "Ocean",
                url = Res.getUri("files/1918465-uhd_2560_1440_24fps.mp4"),
                size = 100
            )
        ),
    )
    init {
        scope.launch {
            if (Flavor.current.clean) {
                settings.putString(KeyCurrentVideo, appVideos.first().videoManifest.filename)
            }
            combine(
                videoStorage.storedVideos(), flowOf(videoApi.fetchManifest())
            ) { storedVideos, manifestEither ->
                storedVideos + manifestEither.fold(
                    ifLeft = { emptyList<VideoInfo>() },
                    ifRight = { videoManifest ->
                        videoManifest.videos.filter { asset ->
                            storedVideos.none {
                                it.videoManifest.filename == asset.filename
                            }
                        }.map { DownloadableVideoInfo(it) }
                    }
                )
            }.collect { combinedVideos ->
                _allVideos.update { appVideos + combinedVideos }
                logger.list(_allVideos.value) { "All videos" }
            }
        }
    }

    private val _currentVideo: Flow<Either<NotFound, VideoInfo.PlayableVideoInfo>> =
        settings.getStringOrNullFlow(KeyCurrentVideo)
            .onEach { logger.d { "Current video: $it" } }
            .map { settingsVideoId ->
                getVideoInfo<VideoInfo.PlayableVideoInfo>(settingsVideoId)
            }
            .onEach { logger.d { "Current video: $it" } }

    override val playingVideo: Flow<Either<NotFound, VideoInfo.PlayableVideoInfo>> = _currentVideo

    private val _allVideos = MutableStateFlow<List<VideoInfo>>(appVideos)
    override val allVideos: StateFlow<List<VideoInfo>> = _allVideos

    override suspend fun playVideo(id: String) =
        getVideoInfo<VideoInfo.PlayableVideoInfo>(id)
            .map { videoInfo ->
                settings.putString(KeyCurrentVideo, videoInfo.videoManifest.filename)
            }

    override suspend fun downloadVideo(id: String) =
        getVideoInfo<DownloadableVideoInfo>(id)
            .flatMap { video ->
                videoDownloader.downloadVideo(video.videoManifest).onRight { path ->
                    logger.d { "Downloaded video: ${video.videoManifest.filename} to $path" }
                    videoStorage.storeVideo(
                        CachedVideoInfo(
                            videoManifest = video.videoManifest,
                            storedPath = path
                        )
                    )
                }.mapLeft { Network }
            }

    override suspend fun deleteVideo(id: String): Either<NotFound, Unit> =
        getVideoInfo<CachedVideoInfo>(id).map { videoStorage.deleteVideo(it).onRight { } }

    private inline fun <reified T : VideoInfo> getVideoInfo(id: String?) =
        _allVideos.value.filterIsInstance<T>()
            .find { it.videoManifest.filename == id }
            ?.right()
            ?: NotFound.left().onLeft { logger.e { "Video not found: $id" } }
}
