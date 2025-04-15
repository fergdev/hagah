package com.fergdev.hagah.screens.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fergdev.hagah.data.VideoRepository
import com.fergdev.hagah.ui.HCard
import org.koin.compose.koinInject

@Composable
fun VideoSelectionScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Center
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val videoRepository = koinInject<VideoRepository>()
            val videos = videoRepository.availableVideos.collectAsState()
            videos.value.forEach {
                HCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = { videoRepository.setVideo(it) }
                        )
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
                        Text(text = it.name)
                    }
                }
            }
        }
    }
}
