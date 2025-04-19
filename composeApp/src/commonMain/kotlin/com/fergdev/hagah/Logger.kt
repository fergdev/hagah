package com.fergdev.hagah

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

internal fun initLogging() {
    if (Flavor.Release.notEnabled) {
        Napier.base(DebugAntilog())
        logBuildInfo()
    }
}

interface TaggedLogger {
    fun w(block: () -> String)
    fun d(block: () -> String)
    fun e(block: () -> String)
    fun e(message: String, throwable: Throwable)
    fun e(throwable: Throwable, message: () -> String)
    fun <T> list(items: List<T>, block: () -> String)
}

fun logger(tag: String): TaggedLogger {
    val logger = object : TaggedLogger {
        val actualTag = "Wow: $tag"
        override fun <T> list(items: List<T>, block: () -> String) {
            val logMessage = buildString {
                appendLine(block())
                items.forEach { item ->
                    appendLine(" - $item")
                }
            }
            Napier.d(tag = actualTag, message = logMessage)
        }

        override fun w(block: () -> String) = Napier.w(tag = actualTag, message = block)
        override fun d(block: () -> String) = Napier.d(tag = actualTag, message = block)
        override fun e(block: () -> String) = Napier.e(tag = actualTag, message = block)
        override fun e(message: String, throwable: Throwable) =
            Napier.e(tag = actualTag, message = message, throwable = throwable)

        override fun e(throwable: Throwable, message: () -> String) =
            Napier.e(tag = actualTag, message = message, throwable = throwable)
    }
    return logger
}
