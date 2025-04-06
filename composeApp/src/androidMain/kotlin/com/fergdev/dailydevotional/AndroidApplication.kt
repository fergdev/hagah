package com.fergdev.dailydevotional

import android.app.Application
import com.fergdev.dailydevotional.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
        initKoin()
    }
}
