package com.fergdev.hagah.screens.history

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fergdev.fcommon.util.endOfMonth
import com.fergdev.fcommon.util.endOfWeek
import com.fergdev.fcommon.util.formatToMonthAndYear
import com.fergdev.fcommon.util.nowDate
import com.fergdev.fcommon.util.startOfMonth
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.DataRepository
import com.fergdev.hagah.screens.history.HistoryDay.Blank
import com.fergdev.hagah.screens.history.HistoryDay.Future
import com.fergdev.hagah.screens.history.HistoryDay.HasHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus

internal sealed interface HistoryState {
    data object Loading : HistoryState

    @Stable
    data class Loaded(val historyMonths: List<HistoryMonth>) : HistoryState

    @Stable
    data object Empty : HistoryState
}

@Stable
internal data class HistoryMonth(
    val title: String,
    val list: List<HistoryDay>
)

internal sealed interface HistoryDay {
    val dayOfMonth: Int

    // Day of week that is in the next month
    data class Blank(override val dayOfMonth: Int) : HistoryDay

    // Future day of the month
    data class Future(override val dayOfMonth: Int) : HistoryDay

    // Day that could have usage
    @Stable
    data class HasHistory(
        override val dayOfMonth: Int,
        val id: Long
    ) : HistoryDay

    @Stable
    data class NoHistory(override val dayOfMonth: Int) : HistoryDay
}

internal class HistoryViewModel(
    private val repository: DataRepository,
    private val clock: Clock,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    val state = MutableStateFlow<HistoryState>(HistoryState.Loading)

    inline fun launch(crossinline block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(dispatcher) { block() }

    init {
        launch {
            state.value = createHistory(repository.history().first<List<DailyHagah>>())
        }
    }

    fun onViewHistory(day: HasHistory) {
        launch {
            repository.setDevotional(day.id)
        }
    }

    private fun createHistory(history: List<DailyHagah>): HistoryState {
        if (history.isEmpty()) {
            return HistoryState.Empty
        }
        val sortedHistory = history.sortedByDescending { it.date }
        var dateIndex = clock.nowDate()
        val lastDate = sortedHistory.last().date
        val uiList = mutableListOf<HistoryMonth>()

        while (dateIndex >= lastDate) {
            val monthDays = mutableListOf<HistoryDay>()

            // Fill the list with filled days
            val lastDayOfMonth = dateIndex.endOfMonth()
            var monthIndex = dateIndex.startOfMonth()
            while (monthIndex <= lastDayOfMonth) {
                if (monthIndex <= dateIndex) {
                    // Possible perf issue for searching
                    val historyItem = sortedHistory.firstOrNull { monthIndex == it.date }
                    monthDays.add(
                        when (historyItem) {
                            null -> HistoryDay.NoHistory(monthIndex.dayOfMonth)
                            else -> {
                                HasHistory(
                                    dayOfMonth = monthIndex.dayOfMonth,
                                    id = historyItem.id
                                )
                            }
                        }
                    )
                } else {
                    monthDays.add(Future(monthIndex.dayOfMonth))
                }
                monthIndex = monthIndex.plus(1, DateTimeUnit.DAY)
            }
            // Pad the list with blank days before the first day of the month
            var endOfWeekIndex = dateIndex.endOfWeek()
            while (endOfWeekIndex > dateIndex) {
                monthDays.add(Blank(dayOfMonth = endOfWeekIndex.dayOfMonth))
                endOfWeekIndex = endOfWeekIndex.minus(1, DateTimeUnit.DAY)
            }

            uiList.add(
                HistoryMonth(
                    title = dateIndex.formatToMonthAndYear(),
                    list = monthDays.reversed()
                )
            )
            dateIndex = dateIndex.startOfMonth().minus(1, DateTimeUnit.DAY)
        }
        return HistoryState.Loaded(uiList)
    }
}
