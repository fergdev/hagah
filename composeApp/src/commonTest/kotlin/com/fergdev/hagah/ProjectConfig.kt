package com.fergdev.hagah

import io.kotest.core.config.AbstractProjectConfig

object ProjectConfig : AbstractProjectConfig() {
    override var coroutineTestScope = true
    override val globalAssertSoftly = true
}
