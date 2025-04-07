package com.fergdev.fcommon.platform

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

private val supportsVibration = arrayOf(OS.Android)

public actual val platformCapabilities: PlatformCapabilities = PlatformCapabilities(
    canSystemDynamicTheme = false,
    canVibrate = hostOs in supportsVibration,
    hasAnalytics = false,
    hasOwnSplashScreen = false,
    canOpenOsSettings = false,
)
