package com.fergdev.hagah.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.fergdev.hagah.data.DailyDevotional
import com.fergdev.hagah.data.DailyDevotionalRepository
import com.fergdev.hagah.data.api.DailyDevotionalApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val dailyDevotionalRepository: DailyDevotionalRepository) :
    ViewModel() {
    sealed interface State {
        data object Loading : State
        data class Error(val message: String) : State
        data class Success(
            val dailyDevotional: DailyDevotional,
            // TODO immutable list
            val history: List<DailyDevotional> = emptyList()
        ) : State
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

            combine(
                dailyDevotionalRepository.history(),
                dailyDevotionalRepository.requestDailyDevotional()
            ) { history, today ->
                return@combine when (today) {
                    is Either.Left -> State.Success(today.value, history)
                    is Either.Right -> when (today.value) {
                        is DailyDevotionalApi.DevotionalError.Network ->
                            State.Error("Network Error, please check your connection.")

                        is DailyDevotionalApi.DevotionalError.Other ->
                            State.Error("Unknown error, please try again.")
                    }
                }
            }.collect { success ->
                state.update { success }
            }
        }
    }
}
