package com.fergdev.hagah.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalWindowInfo
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
    player.loop = true
    player.volume = 0.0f
    player.openUri(url)
    player.play()

    val width = player.metadata.width ?: 0
    val height = player.metadata.height ?: 0
    Napier.d(message = "width: $width, height: $height")
    val windowInfo = LocalWindowInfo.current
    val containerWidth = windowInfo.containerSize.width
    val containerHeight = windowInfo.containerSize.height
    Napier.d(message = "containerWidth: $containerWidth, containerHeight: $containerHeight")

    val scale =
        if (width > 0 && height > 0) {
            min(
                containerWidth.toFloat() / width.toFloat(),
                containerHeight.toFloat() / height.toFloat()
            )
        } else 1.5f
    Napier.d(message = "scale: $scale")
    VideoPlayerSurface(
        playerState = player,
        modifier = modifier.scale(scale)
    )
}
