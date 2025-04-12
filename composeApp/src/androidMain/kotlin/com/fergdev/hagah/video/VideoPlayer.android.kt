package com.fergdev.hagah.video

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.fergdev.hagah.LocalHazeState
import dev.chrisbanes.haze.hazeSource
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject

@SuppressLint("InflateParams")
@UnstableApi
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
) {
    val context: Context = koinInject<Context>()
    val currentExoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }
    LaunchedEffect(url) {
        Napier.d("URL $url")
        val mediaItem = MediaItem.fromUri(url)
        currentExoPlayer.setMediaItem(mediaItem)
        currentExoPlayer.prepare()
        currentExoPlayer.playWhenReady = true
        currentExoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose {
            Napier.d("DisposableEffect")
            currentExoPlayer.release()
        }
    }
    AndroidView(
        factory = { viewBlockContext ->
            val inflater = LayoutInflater.from(viewBlockContext)
            (inflater.inflate(com.fergdev.hagah.R.layout.exo, null) as PlayerView).apply {
                player = currentExoPlayer
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                useController = false
            }
        },
        update = {
        },
        modifier = modifier
            .fillMaxSize()
            .hazeSource(LocalHazeState.current),
        onRelease = {}
    )
}
