package com.fergdev.hagah.server.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal fun Clock.nowDate() = now().toLocalDateTime(TimeZone.currentSystemDefault()).date
