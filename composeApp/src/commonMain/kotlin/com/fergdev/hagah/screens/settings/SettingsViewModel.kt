package com.fergdev.hagah.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fergdev.hagah.AppSettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    private val settings: AppSettingsManager
) : ViewModel() {
    data class State(val meditationDuration: Long = 0L)

    private val _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state

    init {
        viewModelScope.launch {
            settings.settings.collect {
                _state.value = State(meditationDuration = it.meditationLength)
            }
        }
    }

    fun setMeditationDuration(duration: Long) {
        viewModelScope.launch {
            settings.setMeditationDuration(duration)
        }
    }
}
