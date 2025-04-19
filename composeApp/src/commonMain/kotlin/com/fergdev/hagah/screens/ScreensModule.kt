package com.fergdev.hagah.screens

import com.fergdev.hagah.screens.history.HistoryViewModel
import com.fergdev.hagah.screens.main.MainViewModel
import com.fergdev.hagah.screens.settings.SettingsViewModel
import com.fergdev.hagah.screens.video.VideoSelectionViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val viewModelModule = module {
    single { Dispatchers.Main }.bind<CoroutineDispatcher>()
    factoryOf(::MainViewModel)
    factoryOf(::HistoryViewModel)
    factoryOf(::SettingsViewModel)
    factoryOf(::VideoSelectionViewModel)
}
