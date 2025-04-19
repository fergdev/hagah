package com.fergdev.hagah.data.video

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Foundation.NSFileManager
import platform.Foundation.NSOutputStream
import platform.Foundation.NSStreamStatusOpen
import platform.Foundation.outputStreamToFileAtPath

class VideoFileSystemIos : VideoFileSystem {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun writeStreamToFile(
        channel: ByteReadChannel,
        outputPath: String,
        onBytesTransferred: (Long) -> Unit
    ) {
        val outputStream = NSOutputStream.outputStreamToFileAtPath(outputPath, true)
            ?: error("Unable to create output stream for $outputPath")

        outputStream.open()
        if (outputStream.streamStatus != NSStreamStatusOpen) {
            error("Could not open output stream: ${outputStream.streamError?.localizedDescription}")
        }

        try {
            val buffer = ByteArray(4096)
            var totalBytes = 0L

            while (!channel.isClosedForRead) {
                val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                if (bytesRead <= 0) break

                memScoped {
                    buffer.usePinned { pinned ->
                        val bytesWritten = outputStream.write(
                            pinned.addressOf(0).reinterpret(),
                            bytesRead.toULong()
                        )
                        if (bytesWritten < 0) {
                            error("Failed to write to output stream")
                        }
                    }
                }

                totalBytes += bytesRead
                onBytesTransferred(totalBytes)
            }
        } finally {
            outputStream.close()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun deleteVideo(path: String) {
        val fileManager = NSFileManager.defaultManager
        val success = fileManager.removeItemAtPath(path, null)
        if (!success) {
            println("Failed to delete file at $path")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun listCacheDir(path: String): List<String> {
        val fileManager = NSFileManager.defaultManager
        val contents = fileManager.contentsOfDirectoryAtPath(path, null)
            ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        return (contents as List<*>).filterIsInstance<String>()
    }
}
