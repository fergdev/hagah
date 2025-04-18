package com.fergdev.hagah

internal enum class Flavor {
    Debug, Local, Mock, Release;

    companion object {
        val current: Flavor = when (BuildFlags.flavorKey) {
            "debug" -> Debug
            "local" -> Local
            "mock" -> Mock
            "release" -> Release
            else -> error("Unknown flavor: ${BuildFlags.flavorKey}")
        }
    }
}

internal fun logBuildInfo() {
    val logger = logger("Build Info")
    logger.d {
        with(BuildFlags) {
            buildString {
                appendLine("*** BuildInfo ***")
                appendLine("Flavor : ${Flavor.current}")
                appendLine("BuildFlags(")
                appendLine("appName = '$appName',")
                appendLine("versionName = '$versionName',")
                appendLine("privacyPolicyUrl = '$privacyPolicyUrl',")
                appendLine("supportEmail = '$supportEmail',")
                appendLine("flavorKey = '$flavorKey',")
                appendLine("cleanOnStart = '$cleanOnStart',")
                appendLine("localUrl = '$localUrl',")
                appendLine("prodUrl = '$prodUrl'")
                appendLine(")")
            }
        }
    }
}

internal val Flavor.enabled: Boolean
    get() = Flavor.current == this

internal val Flavor.notEnabled: Boolean
    get() = this.enabled.not()

internal val Flavor.hagahUrl: String
    get() = when (this) {
        Flavor.Debug -> BuildFlags.prodUrl
        Flavor.Mock -> ""
        Flavor.Release -> BuildFlags.prodUrl
        Flavor.Local -> BuildFlags.localUrl
    }

internal val Flavor.clean: Boolean
    get() = Flavor.Release.notEnabled && BuildFlags.cleanOnStart.isNotEmpty()

internal fun fError(message: String) {
    if (Flavor.Release.notEnabled) {
        error(message)
    }
}
internal fun fRequire(message: String) {
    if (Flavor.Release.notEnabled) {
        error(message)
    }
}
