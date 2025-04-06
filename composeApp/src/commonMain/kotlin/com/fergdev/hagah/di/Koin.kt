package com.fergdev.hagah.di

import com.fergdev.hagah.data.dataModule
import com.fergdev.hagah.screens.viewModelModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
