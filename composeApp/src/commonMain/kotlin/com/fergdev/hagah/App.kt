package com.fergdev.hagah

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.fergdev.fcommon.ui.widgets.FiveWaySwipeableScreen
import com.fergdev.fcommon.ui.widgets.Screen
import com.fergdev.fcommon.ui.widgets.Screen.DOWN
import com.fergdev.fcommon.ui.widgets.Screen.LEFT
import com.fergdev.fcommon.ui.widgets.Screen.MAIN
import com.fergdev.fcommon.ui.widgets.Screen.RIGHT
import com.fergdev.fcommon.ui.widgets.Screen.UP
import com.fergdev.hagah.data.video.VideoRepository
import com.fergdev.hagah.screens.history.HistoryScreen
import com.fergdev.hagah.screens.main.MainContent
import com.fergdev.hagah.screens.promo.AppPromoContent
import com.fergdev.hagah.screens.settings.main.SettingsContent
import com.fergdev.hagah.screens.theme.AppTheme
import com.fergdev.hagah.screens.video.VideoSelectionContent
import com.fergdev.hagah.share.share
import com.fergdev.hagah.ui.LongPressBox
import com.fergdev.hagah.video.VideoPlayer
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    initLogging()
    AppTheme {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            AppBackground()
            LongPressBox(onAction = { share(ImageBitmap(1, 1)) }) {
                FiveWaySwipeableScreen(modifier = Modifier.fillMaxSize()) {
                    when (it) {
                        MAIN -> MainContent(Modifier.fillMaxSize())
                        UP -> SettingsContent(Modifier.fillMaxSize().gradient(UP))
                        RIGHT -> AppPromoContent(Modifier.fillMaxSize().gradient(RIGHT))
                        DOWN -> HistoryScreen(Modifier.fillMaxSize().gradient(DOWN))
                        LEFT -> VideoSelectionContent(Modifier.fillMaxSize().gradient(LEFT))
                    }
                }
            }
        }
    }
}

private val GradientColors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
private fun Modifier.gradient(screen: Screen): Modifier =
    when (screen) {
        UP -> background(brush = Brush.verticalGradient(colors = GradientColors))
        DOWN -> background(brush = Brush.verticalGradient(colors = GradientColors.reversed()))
        LEFT -> background(brush = Brush.horizontalGradient(colors = GradientColors))
        RIGHT -> background(brush = Brush.horizontalGradient(colors = GradientColors.reversed()))
        MAIN -> this
    }

private sealed interface BackGroundState {
    data object Loading : BackGroundState
    data class Video(val url: String) : BackGroundState
    data object NotFound : BackGroundState
}

@Composable
private fun AppBackground() {
    val videoRepository = koinInject<VideoRepository>()
    var videoInfo by remember { mutableStateOf<BackGroundState>(BackGroundState.Loading) }
    LaunchedEffect(Unit) {
        videoRepository.playingVideo.collect { playingVideo ->
            videoInfo = playingVideo.fold(ifLeft = {
                BackGroundState.NotFound
            }, ifRight = {
                BackGroundState.Video(it.playPath())
            })
        }
    }
    when (val state = videoInfo) {
        BackGroundState.Loading -> {
            Surface(modifier = Modifier.fillMaxSize()) {
                Text("Loading")
            }
        }

        BackGroundState.NotFound -> {
            Surface(modifier = Modifier.fillMaxSize()) {
                Text("Video not found")
            }
        }

        is BackGroundState.Video -> VideoPlayer(modifier = Modifier.fillMaxSize(), url = state.url)
    }
}
