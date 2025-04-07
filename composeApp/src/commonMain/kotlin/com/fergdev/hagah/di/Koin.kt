package com.fergdev.hagah.di

import com.fergdev.hagah.data.dataModule
import com.fergdev.hagah.screens.viewModelModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

inline fun startKoin(
    modules: List<Module> = emptyList(),
    crossinline configure: KoinAppDeclaration = { }
): KoinApplication = org.koin.core.context.startKoin {
    Napier.base(DebugAntilog())
    modules(modules + sharedModule)
    configure()
    createEagerInstances()
}

val sharedModule = module {
    includes(dataModule)
    includes(viewModelModule)
}
