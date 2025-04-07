package com.fergdev.fcommon.platform

import android.os.Build

public actual val platformCapabilities : PlatformCapabilities =
    PlatformCapabilities(
        canSystemDynamicTheme = supportsDynamicColors,
        canVibrate = true,
        hasAnalytics = true,
        hasOwnSplashScreen = true,
        canOpenOsSettings = true
    )

internal val supportsDynamicColors get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
