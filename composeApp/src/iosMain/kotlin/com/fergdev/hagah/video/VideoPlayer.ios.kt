package com.fergdev.hagah.video

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import io.github.aakira.napier.Napier
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.AVFoundation.AVAsset
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerLooper
import platform.AVFoundation.AVQueuePlayer
import platform.AVFoundation.play
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.CoreMedia.CMTimeRange
import platform.CoreMedia.CMTimeRangeMake
import platform.Foundation.NSURL
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIView

@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
) {
    IosVideoPlayer(
        modifier = modifier,
        url = url,
    )
}

@OptIn(ExperimentalForeignApi::class)
fun createTimeRange(startSeconds: Double, durationSeconds: Double): CValue<CMTimeRange> =
    memScoped {
        val start = CMTimeMakeWithSeconds(startSeconds, preferredTimescale = 600)
        val duration = CMTimeMakeWithSeconds(durationSeconds, preferredTimescale = 600)
        CMTimeRangeMake(start, duration)
    }

@OptIn(ExperimentalForeignApi::class)
@Composable
fun IosVideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
) {
    // Create AVQueuePlayer
    val queuePlayer = remember {
        Napier.d { "Creating AVQueuePlayer" }
        AVQueuePlayer()
    }

    // Create AVPlayerViewController
    val avPlayerViewController = remember {
        Napier.d { "Creating AVPlayerViewController" }
        AVPlayerViewController().apply {
            showsPlaybackControls = false
            allowsPictureInPicturePlayback = false
            videoGravity = AVLayerVideoGravityResizeAspectFill
            player = queuePlayer
        }
    }

    val validUrl = remember(url) { NSURL.URLWithString(url) }

    // Create AVPlayerItem
    val playerItem = remember(url) {
        Napier.d { "Creating AVPlayerItem with URL: $url" }
        AVPlayerItem(uRL = validUrl!!)
    }
    var previousItem by remember { mutableStateOf<AVPlayerItem?>(null) }
    var previousLooper by remember { mutableStateOf<AVPlayerLooper?>(null) }

    // Create AVPlayerLooper for looping
    remember(url) {
        Napier.d { "Creating AVPlayerLooper" }
        previousLooper?.disableLooping()
        queuePlayer.insertItem(playerItem, previousItem)
        Napier.d { "Creating AVPlayerLooper 2" }
        queuePlayer.advanceToNextItem()
        previousItem = playerItem
    }

    LaunchedEffect(url) {
        val asset = AVAsset.assetWithURL(validUrl!!)
        asset.loadValuesAsynchronouslyForKeys(listOf("duration")) {
            val duration = asset.duration
            val durationSeconds = CMTimeGetSeconds(duration)
            Napier.d { "Duration of the asset: $durationSeconds s" }
            val timeRange = createTimeRange(startSeconds = 0.0, durationSeconds = durationSeconds)
            previousItem?.let { queuePlayer.removeItem(it) }
//            while (queuePlayer.items().size != 1) {
//                Napier.d { "Removing item from queue" }
//            }
            previousLooper = AVPlayerLooper(
                player = queuePlayer,
                templateItem = playerItem,
                timeRange = timeRange
            )
        }
    }

    UIKitView(
        factory = {
            val playerContainer = UIView()

            avPlayerViewController.view.translatesAutoresizingMaskIntoConstraints = false
            playerContainer.addSubview(avPlayerViewController.view)

            NSLayoutConstraint.activateConstraints(
                listOf(
                    avPlayerViewController.view.leadingAnchor.constraintEqualToAnchor(
                        playerContainer.leadingAnchor
                    ),
                    avPlayerViewController.view.trailingAnchor.constraintEqualToAnchor(
                        playerContainer.trailingAnchor
                    ),
                    avPlayerViewController.view.topAnchor.constraintEqualToAnchor(playerContainer.topAnchor),
                    avPlayerViewController.view.bottomAnchor.constraintEqualToAnchor(playerContainer.bottomAnchor)
                )
            )

            playerContainer
        },
        modifier = modifier.fillMaxSize(),
        update = { _ ->
            Napier.d(message = "Update")
            queuePlayer.play()
        },
        onRelease = {
            Napier.d(message = "Release")
        },
        properties = UIKitInteropProperties(
            isInteractive = false,
            isNativeAccessibilityEnabled = false,
        )
    )
}
