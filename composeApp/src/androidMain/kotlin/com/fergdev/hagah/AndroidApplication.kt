package com.fergdev.hagah

import android.app.Application
import com.fergdev.hagah.di.nonWasmModule
import com.fergdev.hagah.di.startKoin
import org.koin.android.ext.koin.androidContext

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(modules = listOf(nonWasmModule)) builder@{
            androidContext(this@AndroidApplication)
        }
    }
}
