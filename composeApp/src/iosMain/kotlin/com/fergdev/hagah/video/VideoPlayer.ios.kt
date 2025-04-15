package com.fergdev.hagah.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import io.github.aakira.napier.log
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

private const val LogTag = "IosVideoPlayer"

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
) {
    // Create AVQueuePlayer
    val queuePlayer = remember {
        log(tag = LogTag) { "Creating AVQueuePlayer" }
        AVQueuePlayer()
    }
    // Create AVPlayerViewController
    val avPlayerViewController = remember {
        log(tag = LogTag) { "Creating AVPlayerViewController" }
        AVPlayerViewController().apply {
            showsPlaybackControls = false
            allowsPictureInPicturePlayback = false
            videoGravity = AVLayerVideoGravityResizeAspectFill
            player = queuePlayer
        }
    }
    val validUrl = remember(key1 = url) { NSURL.URLWithString(URLString = url) }
    // Create AVPlayerItem
    val playerItem = remember(key1 = url) {
        log(tag = LogTag) { "Creating AVPlayerItem with URL: $url" }
        AVPlayerItem(uRL = validUrl!!)
    }
    var previousItem by remember<MutableState<AVPlayerItem?>> { mutableStateOf(null) }
    var previousLooper by remember<MutableState<AVPlayerLooper?>> { mutableStateOf(null) }
    // Create AVPlayerLooper for looping
    remember(key1 = url) {
        log(tag = LogTag) { "Creating AVPlayerLooper" }
        previousLooper?.disableLooping()
        queuePlayer.insertItem(playerItem, previousItem)
        log(tag = LogTag) { "Creating AVPlayerLooper 2" }
        queuePlayer.advanceToNextItem()
        previousItem = playerItem
    }
    LaunchedEffect(key1 = url) {
        val asset = AVAsset.assetWithURL(validUrl!!)
        asset.loadValuesAsynchronouslyForKeys(listOf("duration")) {
            val duration = asset.duration
            val durationSeconds = CMTimeGetSeconds(duration)
            log(tag = LogTag) { "Duration of the asset: $durationSeconds s" }
            val timeRange = createTimeRange(startSeconds = 0.0, durationSeconds = durationSeconds)
            previousItem?.let { queuePlayer.removeItem(it) }
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
        update = { _ ->
            log(tag = LogTag) { "Update" }
            queuePlayer.play()
        },
        onRelease = {
            log(tag = LogTag) { "Release" }
        },
        modifier = modifier,
        properties = UIKitInteropProperties(
            isInteractive = false,
            isNativeAccessibilityEnabled = false,
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
fun createTimeRange(startSeconds: Double, durationSeconds: Double): CValue<CMTimeRange> =
    memScoped {
        val start = CMTimeMakeWithSeconds(startSeconds, preferredTimescale = 600)
        val duration = CMTimeMakeWithSeconds(durationSeconds, preferredTimescale = 600)
        CMTimeRangeMake(start, duration)
    }
