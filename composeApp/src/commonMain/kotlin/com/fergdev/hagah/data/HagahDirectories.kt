package com.fergdev.hagah.data

interface HagahDirectories {
    fun dataDir(): String
    fun videoCacheDir(): String

    companion object {
        const val dataDir = "data"
        const val VideoDir = "video"
    }
}
