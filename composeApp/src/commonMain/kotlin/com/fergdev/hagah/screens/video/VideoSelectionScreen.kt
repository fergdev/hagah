package com.fergdev.hagah.screens.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fergdev.hagah.LocalHazeState
import com.fergdev.hagah.data.VideoRepository
import com.fergdev.hagah.ui.FazeDirection
import com.fergdev.hagah.ui.faze
import com.fergdev.hagah.ui.fazeGradient
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import org.koin.compose.koinInject

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun VideoSelectionScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .hazeSource(state = LocalHazeState.current, zIndex = 1f)
            .fazeGradient(FazeDirection.Left),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            val videoRepository = koinInject<VideoRepository>()
            val videos = videoRepository.availableVideos.collectAsState()
            videos.value.forEach {
                Box(
                    modifier = Modifier.padding(16.dp)
                        .fillMaxWidth()
                        .faze()
                        .clickable(onClick = { videoRepository.setVideo(it) }),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = it.name)
                    }
                }
            }
        }
    }
}
