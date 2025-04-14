package com.fergdev.hagah.screens.main

import androidx.compose.runtime.Stable
import arrow.core.Either
import com.fergdev.fcommon.util.formatToDayMonthAndYear
import com.fergdev.hagah.AppSettingsManager
import com.fergdev.hagah.FViewModel
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.DataRepository
import com.fergdev.hagah.screens.main.MainViewModel.State.Error
import com.fergdev.hagah.screens.main.MainViewModel.State.Loading
import com.fergdev.hagah.screens.main.MainViewModel.State.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge

internal class MainViewModel(
    private val dataRepository: DataRepository,
    private val appSettings: AppSettingsManager,
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) : FViewModel<MainViewModel.State, Nothing>(Loading, dispatcher) {
    sealed interface State {
        data object Loading : State
        data class Error(val message: String) : State

        @Stable
        data class Success(
            val date: String,
            val dailyHagah: DailyHagah,
            val meditationTime: Long
        ) : State
    }

    private val currentHagahFlow = MutableSharedFlow<Either<DataRepository.Error, DailyHagah>>()

    init {
        launch {
            combine(
                appSettings.settings,
                merge(currentHagahFlow, dataRepository.lookBackHagah)
            ) { settings, dailyHagahEither ->
                dailyHagahEither.fold<State>(
                    ifLeft = { it.mapRepositoryError() },
                    ifRight = { dailyHagah ->
                        Success(
                            date = dailyHagah.date.formatToDayMonthAndYear(),
                            dailyHagah = dailyHagah,
                            meditationTime = settings.meditationLength
                        )
                    }
                )
            }.collect { updateState { it } }
        }
        request()
    }

    fun retry() {
        request()
    }

    private fun request() {
        launch {
            updateState { Loading }
            currentHagahFlow.emit(dataRepository.requestDailyHagah())
        }
    }

    private fun DataRepository.Error.mapRepositoryError() = when (this) {
        is DataRepository.Error.Server -> Error("We can't get your data right now, please try again.")
        is DataRepository.Error.Network -> Error("There was a network connection issue")
        is DataRepository.Error.NotFound -> Error("We could not find that Haga in your history.")
    }
}
