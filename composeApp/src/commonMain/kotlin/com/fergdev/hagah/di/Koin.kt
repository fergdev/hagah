package com.fergdev.hagah.di

import com.fergdev.hagah.data.dataModule
import com.fergdev.hagah.screens.viewModelModule
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

inline fun startKoin(
    modules: List<Module> = emptyList(),
    crossinline configure: KoinAppDeclaration = { }
): KoinApplication = org.koin.core.context.startKoin {
    modules(modules + sharedModule)
    configure()
    createEagerInstances()
}

val sharedModule = module {
    includes(dataModule)
    includes(viewModelModule)
}

