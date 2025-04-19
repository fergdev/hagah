package com.fergdev.hagah.data.video

import io.ktor.utils.io.ByteReadChannel

class VideoFileSystemWasm : VideoFileSystem {
    override fun deleteVideo(path: String) {
    }
    override fun listCacheDir(path: String): List<String> = emptyList()
    override suspend fun writeStreamToFile(
        channel: ByteReadChannel,
        outputPath: String,
        onBytesTransferred: (Long) -> Unit
    ) {
    }
}
