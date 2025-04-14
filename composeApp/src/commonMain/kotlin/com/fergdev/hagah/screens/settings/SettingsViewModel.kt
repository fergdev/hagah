package com.fergdev.hagah.screens.settings

import androidx.lifecycle.viewModelScope
import com.fergdev.hagah.AppSettingsManager
import com.fergdev.hagah.FViewModel
import com.fergdev.hagah.screens.settings.SettingsViewModel.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    private val settings: AppSettingsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) : FViewModel<State, Nothing>(initialState = State(), dispatcher = dispatcher) {
    data class State(val meditationDuration: Long = 0L)

    init {
        viewModelScope.launch {
            settings.settings.collect {
                updateState { State(meditationDuration = it.meditationLength) }
            }
        }
    }

    fun setMeditationDuration(duration: Long) {
        launch { settings.setMeditationDuration(duration) }
    }
}
