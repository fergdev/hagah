package com.fergdev.fcommon.formatting

private const val KB = 1024L
private const val MB = KB * 1024
private const val GB = MB * 1024
private const val TB = GB * 1024

public fun Long.formatSize(): String {
    val size = this
    val (value, unit) = when {
        size < KB -> size to "B"
        size < MB -> size / KB to "KB"
        size < GB -> size / MB to "MB"
        size < TB -> size / GB to "GB"
        else -> size / TB to "TB"
    }

    return "${value.toInt()} $unit"
}
