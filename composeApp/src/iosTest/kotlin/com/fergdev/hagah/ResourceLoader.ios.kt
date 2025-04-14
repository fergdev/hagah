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
    val pathParts = resourceName.split("[.|/]".toRegex())
    println(pathParts)
//    val path = NSBundle.mainBundle.pathForResource("/resources/${pathParts[0]}", pathParts[1])
    val path = "${NSBundle.mainBundle.bundlePath}/resources/$resourceName"
    println("Bundle path $path")
    requireNotNull(path) {
        "Error loading resource $path"
    }
//    val file: CPointer<FILE>? = fopen(path, "r")
//    val size = ftell(file)
//    rewind(file)
//    return memScoped {
//        val tmp = allocArray<ByteVar>(size)
//        fread(tmp, sizeOf<ByteVar>().convert(), size.convert(), file)
//        tmp.toKString()
//    }
    return memScoped {
        val error = alloc<ObjCObjectVar<NSError?>>()
        NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, error.ptr)
            ?: error("$path: Read failed: ${error.value}")
    }
}
