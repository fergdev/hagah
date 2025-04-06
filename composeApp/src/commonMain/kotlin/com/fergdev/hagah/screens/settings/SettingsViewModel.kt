package com.fergdev.hagah.screens.settings

import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    sealed interface State {
        data object Loading : State
        data class Error(val message: String) : State
        data class Success(val text: String) : State
    }
}
