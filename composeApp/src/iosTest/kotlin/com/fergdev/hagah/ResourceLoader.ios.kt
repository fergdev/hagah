package com.fergdev.hagah

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun loadTestResource(resourceName: String): String {
    val path = "${NSBundle.mainBundle.bundlePath}/resources/$resourceName"
    requireNotNull(path) { "Error loading resource $path" }
    return memScoped {
        val error = alloc<ObjCObjectVar<NSError?>>()
        NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, error.ptr)
            ?: error("$path: Read failed: ${error.value}")
    }
}
