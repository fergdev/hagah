package com.fergdev.hagah.video

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.fergdev.hagah.logger
import org.koin.compose.koinInject

private val logger = logger(tag = "")

@UnstableApi
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
) {
    val context: Context = koinInject<Context>()
    val currentExoPlayer = remember {
        ExoPlayer.Builder(context).apply {
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    5_000, // minBufferMs
                    15_000, // maxBufferMs
                    1_000, // bufferForPlaybackMs
                    2_000 // bufferForPlaybackAfterRebufferMs
                )
                .build()
            setLoadControl(loadControl)
        }.build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }
    LaunchedEffect(url) {
        logger.d { "Playing: $url" }
        val mediaItem = MediaItem.fromUri(url)
        currentExoPlayer.setMediaItem(mediaItem)
        currentExoPlayer.prepare()
        currentExoPlayer.playWhenReady = true
        currentExoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose {
            logger.d { "Disposing of player" }
            currentExoPlayer.release()
        }
    }
    AndroidView(
        factory = { viewBlockContext ->
            PlayerView(viewBlockContext).apply {
                player = currentExoPlayer
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                useController = false
            }
        },
        update = {},
        modifier = modifier.fillMaxSize(),
        onRelease = {}
    )
}
