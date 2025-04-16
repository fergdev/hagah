package com.fergdev.hagah

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
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
import com.fergdev.hagah.data.VideoInfo
import com.fergdev.hagah.data.VideoRepository
import com.fergdev.hagah.screens.history.HistoryScreen
import com.fergdev.hagah.screens.main.MainScreen
import com.fergdev.hagah.screens.promo.AppPromoContent
import com.fergdev.hagah.screens.settings.main.SettingsContent
import com.fergdev.hagah.screens.theme.AppTheme
import com.fergdev.hagah.screens.video.VideoSelectionScreen
import com.fergdev.hagah.share.share
import com.fergdev.hagah.ui.LongPressBox
import com.fergdev.hagah.video.VideoPlayer
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            AppBackground()
            LongPressBox(onAction = { share(ImageBitmap(1, 1)) }) {
                FiveWaySwipeableScreen(modifier = Modifier.fillMaxSize()) {
                    when (it) {
                        MAIN -> MainScreen(Modifier.fillMaxSize())
                        UP -> SettingsContent(Modifier.fillMaxSize().gradient(UP))
                        RIGHT -> AppPromoContent(Modifier.fillMaxSize().gradient(RIGHT))
                        DOWN -> HistoryScreen(Modifier.fillMaxSize().gradient(DOWN))
                        LEFT -> VideoSelectionScreen(Modifier.fillMaxSize().gradient(LEFT))
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

@Composable
private fun AppBackground() {
    val videoRepository = koinInject<VideoRepository>()
    var videoInfo by remember { mutableStateOf<VideoInfo?>(null) }
    LaunchedEffect(Unit) { videoRepository.currentVideo.collect { videoInfo = it } }
    videoInfo?.let { VideoPlayer(modifier = Modifier.fillMaxSize(), url = it.uri) }
}
