package com.fergdev.hagah.server.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun Clock.nowDate() = now().toLocalDateTime(TimeZone.currentSystemDefault()).date
