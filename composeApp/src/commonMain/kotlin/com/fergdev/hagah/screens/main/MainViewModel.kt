package com.fergdev.hagah.screens.main

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.fergdev.fcommon.util.formatToDayMonthAndYear
import com.fergdev.fcommon.util.nowDate
import com.fergdev.hagah.AppSettingsManager
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.DataRepository
import com.fergdev.hagah.data.api.DailyDevotionalApi
import com.fergdev.hagah.screens.main.MainViewModel.MainScreenState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

internal class MainViewModel(
    private val dataRepository: DataRepository,
    private val appSettings: AppSettingsManager
) :
    ViewModel() {
    sealed interface MainScreenState {
        data object Loading : MainScreenState
        data class Error(val message: String) : MainScreenState

        @Stable
        data class Success(
            val today: String,
            val dailyHagah: DailyHagah,
            val meditationTime: Long
        ) : MainScreenState
    }

    val state = MutableStateFlow<MainScreenState>(MainScreenState.Loading)

    init {
        request()
        viewModelScope.launch {
            combine(
                appSettings.settings,
                dataRepository.lookBackDevotional
            ) { settings, lookBack ->
                Success(
                    today = lookBack.date.formatToDayMonthAndYear(),
                    dailyHagah = lookBack,
                    meditationTime = settings.meditationLength
                )
            }.collect {
                state.update { it }
            }
        }
    }

    fun retry() {
        request()
    }

    private fun request() {
        viewModelScope.launch {
            state.update { MainScreenState.Loading }

            dataRepository.requestDailyDevotional().first { today ->
                val meditationLength = appSettings.settings.first().meditationLength
                val title = Clock.System.nowDate().formatToDayMonthAndYear()
                val loaded = when (today) {
                    is Either.Left -> Success(title, today.value, meditationLength)
                    is Either.Right -> when (today.value) {
                        is DailyDevotionalApi.DevotionalError.Network ->
                            MainScreenState.Error("Network Error, please check your connection.")

                        is DailyDevotionalApi.DevotionalError.Other ->
                            MainScreenState.Error("Unknown error, please try again.")
                    }
                }
                state.value = loaded
                true
            }
        }
    }
}
