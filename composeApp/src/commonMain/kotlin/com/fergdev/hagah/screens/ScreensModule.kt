package com.fergdev.hagah.screens

import com.fergdev.hagah.screens.main.MainViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val viewModelModule = module {
    factoryOf(::MainViewModel)
}
