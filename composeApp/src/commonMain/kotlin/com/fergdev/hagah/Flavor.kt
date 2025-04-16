package com.fergdev.hagah

internal enum class Flavor {
    Debug,
    Mock,
    Release;

    companion object {
        val current: Flavor = when (BuildFlags.flavorKey) {
            "debug" -> Debug
            "mock" -> Mock
            "release" -> Release
            else -> error("Unknown flavor: ${BuildFlags.flavorKey}")
        }
    }
}

internal val Flavor.enabled: Boolean
    get() = Flavor.current == this

internal val Flavor.notEnabled: Boolean
    get() = this.enabled.not()
