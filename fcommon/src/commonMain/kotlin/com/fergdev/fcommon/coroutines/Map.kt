package com.fergdev.fcommon.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public fun <T> Flow<T?>.mapIfNull(t: T): Flow<T> =
    this.map { it ?: t }
