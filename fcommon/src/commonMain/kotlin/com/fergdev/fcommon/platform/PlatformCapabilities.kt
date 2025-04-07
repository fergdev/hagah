package com.fergdev.fcommon.platform

public expect val platformCapabilities: PlatformCapabilities

public data class PlatformCapabilities(
    val canSystemDynamicTheme: Boolean,
    val canVibrate: Boolean,
    val hasAnalytics: Boolean,
    val hasOwnSplashScreen: Boolean,
    val canOpenOsSettings: Boolean
)
