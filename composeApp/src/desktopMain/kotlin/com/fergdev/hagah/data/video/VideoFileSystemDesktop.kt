package com.fergdev.hagah.data.video

import com.fergdev.hagah.logger
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import java.io.File
import java.io.FileOutputStream

class VideoFileSystemDesktop : VideoFileSystem {
    private val logger = logger("VideoStorage.Desktop")

    override fun deleteVideo(path: String) {
        val file = File(path)
        if (file.exists()) {
            val deleted = file.delete()
            if (!deleted) {
                logger.w { "Failed to delete: $path" }
            }
        } else {
            logger.w { "Attempted to delete non-existent file: $path" }
        }
    }

    override fun listCacheDir(path: String): List<String> {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        return dir.list()?.toList() ?: emptyList()
    }

    override suspend fun writeStreamToFile(
        channel: ByteReadChannel,
        outputPath: String,
        onBytesTransferred: (Long) -> Unit
    ) {
        val file = File(outputPath)
        file.parentFile?.mkdirs()

        FileOutputStream(file).use { output ->
            val buffer = ByteArray(4096)
            var totalBytes = 0L
            while (!channel.isClosedForRead) {
                val read = channel.readAvailable(buffer)
                if (read <= 0) break
                output.write(buffer, 0, read)
                totalBytes += read
                onBytesTransferred(totalBytes)
            }
        }
    }
}
