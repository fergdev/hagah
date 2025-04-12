package com.fergdev.hagah.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalWindowInfo
import com.fergdev.hagah.LocalHazeState
import dev.chrisbanes.haze.hazeSource
import io.github.aakira.napier.Napier
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
) {
    Napier.d(message = "VideoPlayer: $url")
    val player = rememberVideoPlayerState()
    LaunchedEffect(url) {
        with(player) {
            openUri(url)
            play()
            loop = true
        }
    }
    val width = player.metadata.width ?: 0
    val height = player.metadata.height ?: 0
    val windowInfo = LocalWindowInfo.current
    val containerWidth = windowInfo.containerSize.width
    val containerHeight = windowInfo.containerSize.height

    val scale =
        if (width > 0 && height > 0) {
            min(
                containerWidth.toFloat() / width.toFloat(),
                containerHeight.toFloat() / height.toFloat()
            )
        } else 16f / 9f

    Napier.d(message = "scale: $scale")
    VideoPlayerSurface(
        modifier = Modifier.scale(scale)
            .hazeSource(LocalHazeState.current),
        playerState = player
    )
}
