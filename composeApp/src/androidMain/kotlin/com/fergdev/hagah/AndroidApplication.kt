package com.fergdev.hagah

import android.app.Application
import com.fergdev.hagah.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
        initKoin()
    }
}
