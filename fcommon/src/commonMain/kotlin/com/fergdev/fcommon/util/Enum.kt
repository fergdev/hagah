package com.fergdev.fcommon.util

public inline fun <reified T : Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}

public inline fun <reified T : Enum<T>> T.previous(): T {
    val values = enumValues<T>()
    val nextOrdinal = ordinal - 1
    return if (nextOrdinal < 0) values[values.size - 1]
    else values[nextOrdinal]
}
