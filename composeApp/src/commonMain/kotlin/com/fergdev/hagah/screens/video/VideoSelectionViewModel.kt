package com.fergdev.hagah.screens.video

import androidx.compose.runtime.Stable
import com.fergdev.fcommon.formatting.formatSize
import com.fergdev.hagah.FViewModel
import com.fergdev.hagah.data.video.VideoInfo
import com.fergdev.hagah.data.video.VideoRepository
import com.fergdev.hagah.fError
import com.fergdev.hagah.logger
import com.fergdev.hagah.screens.video.VideoSelectionViewModel.DownloadState.DownLoading
import com.fergdev.hagah.screens.video.VideoSelectionViewModel.State.UIVideo
import com.fergdev.hagah.screens.video.VideoSelectionViewModel.State.UIVideo.Downloadable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

internal class VideoSelectionViewModel(
    private val videoRepository: VideoRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) : FViewModel<VideoSelectionViewModel.State, Nothing>(
    initialState = State.Loading, dispatcher = dispatcher
) {
    sealed interface State {
        object Loading : State

        @Stable
        data class Success(
            val playableVideos: List<UIVideo.Playable>,
            val downloadableVideos: List<Downloadable>
        ) : State

        sealed interface UIVideo {
            val title: String
            val id: String

            data class Playable(
                override val title: String,
                override val id: String,
                val playing: Boolean = false,
                val highlighted: Boolean = false,
                val size: String? = null,
                val canDelete: Boolean
            ) : UIVideo

            data class Downloadable(
                override val title: String,
                override val id: String,
                val size: String,
                val isDownloading: Boolean
            ) : UIVideo
        }
    }

    private val logger = logger("VideoSelectionViewModel")

    private sealed interface DownloadState {
        data object Success : DownloadState
        data object DownLoading : DownloadState
        data object Error : DownloadState
    }

    private val downloadingFlow = MutableStateFlow<Map<String, DownloadState>>(emptyMap())

    init {
        launch {
            combine(
                videoRepository.allVideos,
                videoRepository.playingVideo,
                downloadingFlow
            ) { allVideos, playingVideo, downloadingState ->
                val mapped = allVideos.map { info ->
                    val downloadingState = downloadingState[info.id()]
                    val isDownloading = downloadingState is DownLoading
                    val isHighLighted = downloadingState is DownloadState.Success
                    val isPlaying = playingVideo == info
                    val size = if (info is VideoInfo.AppVideoInfo) {
                        "Bundled with app"
                    } else {
                        "Size: ${info.videoManifest.size.formatSize()}"
                    }
                    when (info) {
                        is VideoInfo.DownloadableVideoInfo -> {
                            Downloadable(
                                title = info.videoManifest.title,
                                id = info.id(),
                                size = size,
                                isDownloading = isDownloading
                            )
                        }

                        is VideoInfo.PlayableVideoInfo -> {
                            UIVideo.Playable(
                                title = info.videoManifest.title,
                                id = info.id(),
                                size = size,
                                canDelete = info is VideoInfo.CachedVideoInfo,
                                playing = isPlaying,
                                highlighted = isHighLighted
                            )
                        }
                    }
                }
                State.Success(
                    mapped.filterIsInstance<UIVideo.Playable>().sortedBy { it.title },
                    mapped.filterIsInstance<Downloadable>().sortedBy { it.title }
                )
            }.collect { success ->
                updateState { success }
            }
        }
    }

    private fun updateDownloadingState(
        id: String,
        state: DownloadState?
    ) {
        downloadingFlow.update {
            it.toMutableMap()
                .apply {
                    if (state == null) remove(id)
                    else put(id, state)
                }
        }
    }

    fun onDownload(downloadable: Downloadable) {
        if (downloadable.isDownloading) {
            fError("Already downloading ${downloadable.id}")
            return
        }
        launch {
            updateDownloadingState(downloadable.id, DownLoading)
            videoRepository.downloadVideo(downloadable.id).onLeft {
                updateDownloadingState(downloadable.id, DownloadState.Error)
            }.onRight {
                updateDownloadingState(downloadable.id, DownloadState.Success)
            }
        }
    }

    fun onDelete(playable: UIVideo.Playable) {
        launch {
            updateDownloadingState(playable.id, null)
            videoRepository.deleteVideo(playable.id)
        }
    }

    fun onPlay(playable: UIVideo.Playable) {
        launch {
            updateDownloadingState(playable.id, null)
            videoRepository.playVideo(playable.id)
        }
    }
}
