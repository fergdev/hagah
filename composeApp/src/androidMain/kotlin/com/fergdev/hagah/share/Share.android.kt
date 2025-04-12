package com.fergdev.hagah.share

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.view.PixelCopy
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.graphics.createBitmap
import com.fergdev.fcommon.util.nowDateTime
import com.fergdev.hagah.BuildFlags
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.io.IOException
import org.koin.mp.KoinPlatform

actual fun share(bitmap: ImageBitmap) {
    val context = KoinPlatform.getKoin().inject<ComponentActivity>().value
    val time = Clock.System.nowDateTime().format(LocalDateTime.Formats.ISO)
    try {
        val view = context.window.decorView.rootView

        val bitmap = createBitmap(view.width, view.height)
        try {
            val handlerThread = HandlerThread("PixelCopier")
            handlerThread.start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PixelCopy.request(
                    context.window, bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            val child = "hagah_$time.png"
                            saveImageToGallery(context, bitmap, child)
                        }
                        handlerThread.quitSafely()
                    },
                    Handler(handlerThread.looper)
                )
            }
        } catch (e: IllegalArgumentException) {
            Napier.e("IllegalArgumentException while trying to write file for sharing: ${e.message}")
        }
    } catch (ioException: IOException) {
        Napier.e("IOException while trying to write file for sharing: ${ioException.message}")
    }
}

fun saveImageToGallery(
    context: Context,
    bitmap: Bitmap,
    filename: String = "MyImage_${System.currentTimeMillis()}.png"
) {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/${BuildFlags.appName}"
        ) // Optional subfolder
        put(MediaStore.MediaColumns.IS_PENDING, 1) // Android 10+ requirement while writing
    }

    val imageUri: Uri? =
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
        resolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }

        // Mark the image as ready to be indexed
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share image via"))
    }
}
