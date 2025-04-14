package com.fergdev.hagah.data

import com.russhwolf.settings.coroutines.FlowSettings
import hagah.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

internal interface VideoRepository {
    val currentVideo: Flow<VideoInfo>
    val availableVideos: StateFlow<List<VideoInfo>>
    fun setVideo(video: VideoInfo)
}

internal data class VideoInfo(
    val uri: String,
    val name: String,
    val id: String,
)

private const val KeyPrefix = "VideoManger"
private const val KeyCurrentVideo = "$KeyPrefix:currentVideo"

internal class VideoRepositoryMock(private val settings: FlowSettings) : VideoRepository {
    private val testVideos: List<VideoInfo> = listOf(
        VideoInfo(
            id = "0",
            name = "Ocean",
            uri = Res.getUri("files/1918465-uhd_2560_1440_24fps.mp4")
        ),
        VideoInfo(
            id = "1",
            uri = Res.getUri("files/2235844-hd_720_1280_30fps.mp4"),
            name = "Sunset",
        ),
        VideoInfo(
            id = "3",
            uri = Res.getUri("files/3162713-hd_1920_1080_24fps.mp4"),
            name = "Woods",
        ),
        VideoInfo(
            id = "4",
            uri = Res.getUri("files/4250244-uhd_1440_2160_30fps.mp4"),
            name = "Mountain",
        ),
        VideoInfo(
            id = "5",
            uri = Res.getUri("files/6444883-uhd_3840_2160_30fps.mp4"),
            name = "Lake",
        ),
    )
    private val scope = CoroutineScope(EmptyCoroutineContext)

    private val _currentVideo = settings.getStringOrNullFlow(KeyCurrentVideo)
        .map { settingsVideoId ->
            testVideos.find { it.id == settingsVideoId } ?: testVideos.first()
        }

    override val currentVideo: Flow<VideoInfo> = _currentVideo

    private val _availableVideos = MutableStateFlow(testVideos)
    override val availableVideos: StateFlow<List<VideoInfo>> = _availableVideos

    override fun setVideo(video: VideoInfo) {
        scope.launch { settings.putString(KeyCurrentVideo, video.id) }
    }
}
