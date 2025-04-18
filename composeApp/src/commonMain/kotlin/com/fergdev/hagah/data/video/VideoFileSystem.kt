package com.fergdev.hagah.data.video

import io.ktor.utils.io.ByteReadChannel

interface VideoFileSystem {
    fun deleteVideo(path: String)
    fun listCacheDir(path: String): List<String>
    suspend fun writeStreamToFile(
        channel: ByteReadChannel,
        outputPath: String,
        onBytesTransferred: (Long) -> Unit
    )
}
