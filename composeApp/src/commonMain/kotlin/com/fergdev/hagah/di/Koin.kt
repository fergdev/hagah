package com.fergdev.hagah.di

import com.fergdev.hagah.AppSettingsManager
import com.fergdev.hagah.AppSettingsMangerImpl
import com.fergdev.hagah.Flavor
import com.fergdev.hagah.data.dataModule
import com.fergdev.hagah.notEnabled
import com.fergdev.hagah.screens.viewModelModule
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

inline fun startKoin(
    modules: List<Module> = emptyList(),
    crossinline configure: KoinAppDeclaration = { }
): KoinApplication = org.koin.core.context.startKoin {
    initLogging()
    modules(modules + sharedModule)
    configure()
    createEagerInstances()
}

fun initLogging() {
    if (Flavor.Release.notEnabled) {
        Napier.base(DebugAntilog())
    }
}

val sharedModule = module {
    includes(dataModule)
    includes(viewModelModule)
    single<Clock> { Clock.System }
    single<FlowSettings> {
        Settings()
            .makeObservable()
            .toFlowSettings(dispatcher = Dispatchers.Main)
    }
    singleOf(::AppSettingsMangerImpl).bind<AppSettingsManager>()
}
