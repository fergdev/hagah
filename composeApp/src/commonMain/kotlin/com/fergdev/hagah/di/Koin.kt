package com.fergdev.hagah.di

import com.fergdev.hagah.AppSettingsManager
import com.fergdev.hagah.AppSettingsMangerImpl
import com.fergdev.hagah.data.dataModule
import com.fergdev.hagah.screens.viewModelModule
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

internal class BrowserLogger(level: Level) : Logger(level) {
    override fun display(level: Level, msg: MESSAGE) {
        Napier.d { msg }
    }
}

internal inline fun startKoin(
    modules: List<Module> = emptyList(),
    crossinline configure: KoinAppDeclaration = { }
): KoinApplication = org.koin.core.context.startKoin {
    logger(BrowserLogger(Level.DEBUG)) // Log to console

    modules(modules + sharedModule)
    configure()
    createEagerInstances()
}

@OptIn(ExperimentalTime::class)
val sharedModule = module {
    includes(platformModule)
    includes(dataModule)
    includes(viewModelModule)
    single<kotlin.time.Clock> { kotlin.time.Clock.System }
    single<FlowSettings> {
        Settings()
            .makeObservable()
            .toFlowSettings(dispatcher = Dispatchers.Main)
    }
    singleOf(::AppSettingsMangerImpl).bind<AppSettingsManager>()
}

expect val platformModule: Module
