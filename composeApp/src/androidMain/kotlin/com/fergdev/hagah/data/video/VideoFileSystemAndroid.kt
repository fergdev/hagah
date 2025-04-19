package com.fergdev.hagah.data.video

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.yield
import java.io.File

class VideoFileSystemAndroid : VideoFileSystem {
    override fun deleteVideo(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun listCacheDir(path: String): List<String> {
        val file = File(path)
        return file.list()!!.toList()
    }

    override suspend fun writeStreamToFile(
        channel: ByteReadChannel,
        outputPath: String,
        onBytesTransferred: (Long) -> Unit
    ) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        val file = File(outputPath)
        file.outputStream().use { output ->
            var downloaded = 0L

            while (!channel.isClosedForRead) {
                val bytesRead = channel.readAvailable(buffer)
                output.write(buffer, 0, bytesRead)
                downloaded += bytesRead
                yield()
                onBytesTransferred(downloaded)
            }
        }
    }
}
