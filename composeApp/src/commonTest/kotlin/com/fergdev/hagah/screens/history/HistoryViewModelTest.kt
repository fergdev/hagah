package com.fergdev.hagah.screens.history

import com.fergdev.hagah.asViewModel
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.DataRepository
import com.fergdev.hagah.data.Verse
import com.fergdev.hagah.idle
import com.fergdev.hagah.shouldBeList
import com.fergdev.hagah.testDispatcher
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.kotest.assertions.assertSoftly
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalKotest::class)
class HistoryViewModelTest : FreeSpec({
    asViewModel()
    val clock = mock<Clock>()
    val dataRepository = mock<DataRepository>()
    val vm: TestScope.(block: HistoryViewModel.() -> Unit) -> Unit = { block ->
        HistoryViewModel(
            repository = dataRepository,
            clock = clock,
            dispatcher = testDispatcher
        ).apply { block() }
    }
    val now = LocalDateTime.parse("2023-01-10T00:00:00")
    val nowDateTime = now.date
    every { clock.now() } returns now.toInstant(TimeZone.UTC)
    "HistoryViewModel" - {
        "Initial State" - {
            "should be Loading" {
                this
                everySuspend { dataRepository.history() } returns flowOf(emptyList())
                vm {
                    state.value shouldBe HistoryState.Loading
                }
            }
        }
        "No History" - {
            "should set Empty state when there are no items" {
                everySuspend { dataRepository.history() } returns flowOf(emptyList())
                vm {
                    idle()
                    state.value shouldBe HistoryState.Empty
                }
            }
        }
        "With History" - {
            "should set the state with the history items in order" {
                everySuspend { dataRepository.history() } returns flowOf(history(nowDateTime))
                vm {
                    idle()
                    assertLoaded()
                }
            }
            "should set the state with the history items out of order" {
                everySuspend { dataRepository.history() } returns flowOf(history(nowDateTime).reversed())
                vm {
                    idle()
                    assertLoaded()
                }
            }
        }
    }
})

private fun HistoryViewModel.assertLoaded() {
    assertSoftly {
        (state.value as HistoryState.Loaded).apply {
            historyMonths.zip(WithHistoryResult.historyMonths).forEach {
                it.first.title shouldBe it.second.title
                it.first.list shouldBeList it.second.list
            }
        }
    }
}

private fun history(nowDateTime: LocalDate) = listOf(
    DailyHagah(
        id = 1L,
        date = nowDateTime.minus(2, DateTimeUnit.DAY),
        verse = Verse(reference = "verse1", text = "vers2"),
        reflection = "reflection",
        callToAction = "cta",
        prayer = "prayer",
    ),
    DailyHagah(
        id = 0L,
        date = nowDateTime,
        verse = Verse(reference = "verse1", text = "vers2"),
        reflection = "reflection",
        callToAction = "cta",
        prayer = "prayer",
    ),
)

private val WithHistoryResult = HistoryState.Loaded(
    listOf(
        HistoryMonth(
            title = "JANUARY, 2023",
            list = listOf(
                HistoryDay.Blank(dayOfMonth = 11),
                HistoryDay.Blank(dayOfMonth = 12),
                HistoryDay.Blank(dayOfMonth = 13),
                HistoryDay.Blank(dayOfMonth = 14),
                HistoryDay.Blank(dayOfMonth = 15),
                HistoryDay.Future(dayOfMonth = 31),
                HistoryDay.Future(dayOfMonth = 30),
                HistoryDay.Future(dayOfMonth = 29),
                HistoryDay.Future(dayOfMonth = 28),
                HistoryDay.Future(dayOfMonth = 27),
                HistoryDay.Future(dayOfMonth = 26),
                HistoryDay.Future(dayOfMonth = 25),
                HistoryDay.Future(dayOfMonth = 24),
                HistoryDay.Future(dayOfMonth = 23),
                HistoryDay.Future(dayOfMonth = 22),
                HistoryDay.Future(dayOfMonth = 21),
                HistoryDay.Future(dayOfMonth = 20),
                HistoryDay.Future(dayOfMonth = 19),
                HistoryDay.Future(dayOfMonth = 18),
                HistoryDay.Future(dayOfMonth = 17),
                HistoryDay.Future(dayOfMonth = 16),
                HistoryDay.Future(dayOfMonth = 15),
                HistoryDay.Future(dayOfMonth = 14),
                HistoryDay.Future(dayOfMonth = 13),
                HistoryDay.Future(dayOfMonth = 12),
                HistoryDay.Future(dayOfMonth = 11),
                HistoryDay.HasHistory(dayOfMonth = 10, id = 0),
                HistoryDay.NoHistory(dayOfMonth = 9),
                HistoryDay.HasHistory(dayOfMonth = 8, id = 1),
                HistoryDay.NoHistory(dayOfMonth = 7),
                HistoryDay.NoHistory(dayOfMonth = 6),
                HistoryDay.NoHistory(dayOfMonth = 5),
                HistoryDay.NoHistory(dayOfMonth = 4),
                HistoryDay.NoHistory(dayOfMonth = 3),
                HistoryDay.NoHistory(dayOfMonth = 2),
                HistoryDay.NoHistory(dayOfMonth = 1)
            )
        )
    )
)
