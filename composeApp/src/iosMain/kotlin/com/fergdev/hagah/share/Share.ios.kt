package com.fergdev.hagah.share

import androidx.compose.ui.graphics.ImageBitmap
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Photos.PHAssetCreationRequest
import platform.Photos.PHAssetResourceCreationOptions
import platform.Photos.PHAssetResourceTypePhoto
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

actual fun share(bitmap: ImageBitmap) {
    Napier.d("share")
    val x = captureScreenshot()
    Napier.d("capture ${x == null}")
    val data = uiImageToPngData(x!!)
    Napier.d("data ${data == null}")
    saveImage(data!!, "test", "png")
    Napier.d("finished share")
}

private fun uiImageToPngData(image: UIImage): NSData? = UIImagePNGRepresentation(image)

@OptIn(ExperimentalForeignApi::class)
private fun captureScreenshot(): UIImage? {
    val window = UIApplication.sharedApplication.keyWindow ?: return null
    val bounds = window.bounds
    val size = bounds.useContents { CGSizeMake(size.width, size.height) }
    val renderer = UIGraphicsImageRenderer(size = size)
    return renderer.imageWithActions { context ->
        window.drawViewHierarchyInRect(bounds, afterScreenUpdates = false)
    }
}

private fun saveImage(data: NSData, filename: String, extension: String) {
    Napier.d("save image $filename $extension")
    val url = saveImageToLibrary(data = data, filename = filename, extension = extension)
    Napier.d("URL $filename $extension")
    PHPhotoLibrary.requestAuthorization { status ->
        if (status == PHAuthorizationStatusAuthorized) {
            Napier.d("sharing $url")
            val activityViewController = UIActivityViewController(
                activityItems = listOf(url),
                applicationActivities = null
            )
            MainScope().launch {
                UIApplication.sharedApplication
                    .keyWindow
                    ?.rootViewController
                    ?.presentViewController(
                        activityViewController, animated = true, completion = null
                    )
            }
        } else {
            Napier.d("Permission denied to access photo library")
        }
    }
}

private fun saveImageToLibrary(data: NSData, filename: String, extension: String): NSURL? {
    // Create a temporary file to store the image data
    val tempDirectory = NSTemporaryDirectory()
    val tempFilePath = "$tempDirectory$filename.$extension"

    // Write NSData to the temporary file
    val fileManager = NSFileManager.defaultManager
    if (fileManager.createFileAtPath(tempFilePath, data, null)) {
        val fileURL = NSURL.fileURLWithPath(tempFilePath)

        // Add the image to the Photos library
        PHPhotoLibrary.sharedPhotoLibrary().performChanges({
            val creationRequest = PHAssetCreationRequest.creationRequestForAsset()
            creationRequest.addResourceWithType(
                type = PHAssetResourceTypePhoto,
                fileURL = fileURL,
                options = PHAssetResourceCreationOptions()
            )
        }, { success, error ->
            if (success) {
                Napier.d("Successfully saved image to Photos.")
            } else {
                Napier.d("Failed to save image: ${error?.localizedDescription}")
            }
        })
        return fileURL
    } else {
        Napier.d("Failed to write image data to temporary file.")
    }
    return null
}
