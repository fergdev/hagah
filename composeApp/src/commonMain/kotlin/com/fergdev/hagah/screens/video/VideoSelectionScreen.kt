package com.fergdev.hagah.screens.video

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.AnimatedTracingBorder
import com.fergdev.fcommon.ui.FConstants
import com.fergdev.fcommon.ui.conditional
import com.fergdev.fcommon.ui.effects.animatedGradientBorder
import com.fergdev.fcommon.ui.effects.shimmerEffect
import com.fergdev.fcommon.ui.widgets.PulsingIcon
import com.fergdev.hagah.screens.video.VideoSelectionViewModel.State.Loading
import com.fergdev.hagah.screens.video.VideoSelectionViewModel.State.Success
import com.fergdev.hagah.screens.video.VideoSelectionViewModel.State.UIVideo
import com.fergdev.hagah.ui.HCard
import com.fergdev.hagah.ui.download
import org.koin.compose.koinInject

@Composable
fun VideoSelectionContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier, contentAlignment = Center
    ) {
        val viewModel = koinInject<VideoSelectionViewModel>()
        val state = viewModel.state.collectAsState()
        when (val value = state.value) {
            Loading -> CircularProgressIndicator()
            is Success -> SuccessContent(
                state = value,
                onPlay = viewModel::onPlay,
                onDownload = viewModel::onDownload,
                onDelete = viewModel::onDelete
            )
        }
    }
}

@Composable
private fun SuccessContent(
    state: Success,
    onPlay: (UIVideo.Playable) -> Unit,
    onDownload: (UIVideo.Downloadable) -> Unit,
    onDelete: (UIVideo.Playable) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        item {
            Text(text = "Your videos", style = MaterialTheme.typography.titleLarge)
        }
        items(state.playableVideos, key = { it.id }) { video ->
            AnimatedBox {
                PlayableVideoItem(
                    modifier = Modifier.fillMaxWidth(),
                    info = video,
                    onDelete = { onDelete(video) },
                    onPlay = { onPlay(video) }
                )
            }
        }
        item {
            Text(text = "Available for download", style = MaterialTheme.typography.titleLarge)
        }
        if (state.downloadableVideos.isEmpty()) {
            item {
                AnimatedBox {
                    Text(text = "More content dropping soon")
                }
            }
        }
        items(state.downloadableVideos, key = { it.id }) { video ->
            AnimatedBox {
                DownloadableVideoItem(
                    modifier = Modifier.fillMaxWidth(), info = video
                ) { onDownload(video) }
            }
        }
    }
}

@Composable
private fun LazyItemScope.AnimatedBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth().animateItem(
            fadeInSpec = spring(stiffness = Spring.StiffnessLow),
            placementSpec = spring(
                stiffness = Spring.StiffnessLow,
                visibilityThreshold = IntOffset.VisibilityThreshold
            ),
            fadeOutSpec = spring(stiffness = Spring.StiffnessMedium)
        )
    ) {
        content()
    }
}

@Composable
private fun PlayableVideoItem(
    info: UIVideo.Playable,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onPlay: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    HCard(
        modifier = modifier.conditional(!info.playing || expanded) {
            clickable {
                if (!info.playing && !expanded) {
                    onPlay()
                } else {
                    expanded = false
                }
            }
        }.conditional(info.playing) {
            shimmerEffect()
        }.animatedGradientBorder(
            info.highlighted, highLightedColors()
        ),
        horizontalAlignment = CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Column {
                Text(text = info.title)
                if (expanded) {
                    Text(
                        text = if (info.canDelete) {
                            "Size on disk: ${info.size}"
                        } else {
                            "Bundled with app"
                        }
                    )
                }
            }
            val imageVector = when {
                !expanded -> Icons.Default.KeyboardArrowDown
                info.canDelete -> Icons.Default.Delete
                else -> Icons.Default.KeyboardArrowUp
            }
            val deleteEnabled = expanded && info.canDelete
            Icon(
                imageVector = imageVector,
                contentDescription = "Playable action",
                modifier = Modifier.clickable {
                    if (deleteEnabled) onDelete() else expanded = !expanded
                }
            )
        }
    }
}

@Composable
private fun highLightedColors() = listOf(
    Color.White.copy(alpha = 0.8f),
    MaterialTheme.colorScheme.secondary.copy(alpha = FConstants.Opacity.TINT),
    Color.White.copy(alpha = 0.8f)
)

@Composable
private fun DownloadableVideoItem(
    info: UIVideo.Downloadable,
    modifier: Modifier = Modifier,
    onDownload: () -> Unit
) {
    AnimatedTracingBorder(modifier = modifier, enabled = info.isDownloading) {
        var expanded by remember { mutableStateOf(false) }
        HCard(
            modifier = modifier.conditional(!info.isDownloading) {
                clickable { expanded = !expanded }
            },
            horizontalAlignment = CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Column {
                    Text(text = info.title)
                    if (expanded) {
                        Text(
                            text = info.size,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (expanded) {
                    if (info.isDownloading) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Expand",
                        )
                    } else {
                        PulsingIcon(
                            imageVector = download,
                            contentDescription = "Download",
                            onClick = onDownload,
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                    )
                }
            }
        }
    }
}
