package com.fergdev.fcommon.platform

public actual val platformCapabilities: PlatformCapabilities = PlatformCapabilities(
    canVibrate = true,
    canSystemDynamicTheme = false,
    hasAnalytics = true,
    hasOwnSplashScreen = false,
    canOpenOsSettings = true
)
