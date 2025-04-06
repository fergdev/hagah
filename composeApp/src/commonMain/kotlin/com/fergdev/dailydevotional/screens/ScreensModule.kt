package com.fergdev.dailydevotional.screens

import com.fergdev.dailydevotional.screens.main.MainViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val viewModelModule = module {
    factoryOf(::MainViewModel)
}
