package com.fergdev.hagah.data.video

import arrow.core.Either
import com.fergdev.fcommon.coroutines.mapIfNull
import com.fergdev.hagah.Flavor
import com.fergdev.hagah.clean
import com.fergdev.hagah.data.HagahDirectories
import com.fergdev.hagah.logger
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.extensions.minus
import io.github.xxfast.kstore.extensions.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import kotlin.coroutines.EmptyCoroutineContext

internal interface VideoStorage {
    suspend fun storedVideos(): Flow<List<VideoInfo.CachedVideoInfo>>
    suspend fun storeVideo(video: VideoInfo.CachedVideoInfo)
    suspend fun deleteVideo(video: VideoInfo.CachedVideoInfo): Either<Error.Delete, Unit>

    sealed interface Error {
        data object Delete : Error
    }
}

// Used to avoid di sending the wrong store
const val VideoAssetsDb = "videoAssetsDb"

internal data class VideoStoreWrapper(
    val store: KStore<List<VideoInfo.CachedVideoInfo>>
)

internal class VideoStorageImpl(
    videoStoreWrapper: VideoStoreWrapper,
    private val hagahDirectories: HagahDirectories,
    private val videoFileSystem: VideoFileSystem
) : VideoStorage {
    private val store: KStore<List<VideoInfo.CachedVideoInfo>> =
        videoStoreWrapper.store

    private val logger = logger("VideoStorage")
    private val scope = CoroutineScope(EmptyCoroutineContext)

    init {
        logger.d { "VideoStorage " }
    }

    init {
        if (Flavor.current.clean) {
            logger.d { "Cleaning video store" }
            scope.launch {
                store.updates.first()?.forEach {
                    logger.d { "Deleting video ${it.storedPath}" }
                    deleteVideo(it)
                }
                logger.d { "Checking cache" }
                val videoCacheDir = hagahDirectories.videoCacheDir()
                videoFileSystem.listCacheDir(videoCacheDir).forEach {
                    logger.e { "Extra cached item : $it" }
                    videoFileSystem.deleteVideo(Path(videoCacheDir, it).toString())
                }
                store.set(emptyList())
            }
        }
    }

    override suspend fun storedVideos() = store.updates.mapIfNull(emptyList())
        .onEach { logger.list(it) { "Stored videos: " } }

    override suspend fun storeVideo(video: VideoInfo.CachedVideoInfo) {
        logger.d { "Storing video ${video.videoManifest.filename}" }
        store.plus(video)
    }

    override suspend fun deleteVideo(video: VideoInfo.CachedVideoInfo): Either<VideoStorage.Error.Delete, Unit> =
        Either.catch {
            videoFileSystem.deleteVideo(video.storedPath)
        }.onRight {
            store.minus(video)
        }.mapLeft {
            logger.e(it) { "Error deleting video ${video.videoManifest.filename}" }
            VideoStorage.Error.Delete
        }
}
