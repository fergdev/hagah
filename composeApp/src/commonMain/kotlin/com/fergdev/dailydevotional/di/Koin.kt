package com.fergdev.dailydevotional.di

import com.fergdev.dailydevotional.data.dataModule
import com.fergdev.dailydevotional.screens.viewModelModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
