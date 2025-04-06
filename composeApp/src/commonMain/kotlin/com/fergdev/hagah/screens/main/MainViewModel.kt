package com.fergdev.hagah.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fergdev.hagah.data.DailyDevotional
import com.fergdev.hagah.data.DailyDevotionalApi
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val dailyDevotionalApi: DailyDevotionalApi) : ViewModel() {
    sealed interface State {
        data object Loading : State
        data class Error(val message: String) : State
        data class Success(val dailyDevotional: DailyDevotional) : State
    }
    val state = MutableStateFlow<State>(State.Loading)
    init {
        request()
    }

    fun retry() {
        request()
    }

    private fun request() {
        viewModelScope.launch {
            state.update { State.Loading }
            try {
                state.update { State.Success(dailyDevotionalApi.getData()) }
            } catch (t: IllegalArgumentException) {
                Napier.e(message = "wowow", tag = "ListViewModel", throwable = t)
                state.update { State.Error(t.message ?: "Unknown") }
            }
        }
    }
}
