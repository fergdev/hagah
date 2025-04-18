package com.fergdev.hagah.screens.history

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.conditional
import com.fergdev.fcommon.ui.layouts.Spacer
import com.fergdev.hagah.screens.history.HistoryDay.Blank
import com.fergdev.hagah.screens.history.HistoryDay.Future
import com.fergdev.hagah.screens.history.HistoryDay.HasHistory
import com.fergdev.hagah.screens.history.HistoryDay.NoHistory
import com.fergdev.hagah.ui.HCard
import com.fergdev.hagah.ui.PulsingText
import io.github.aakira.napier.log
import kotlinx.datetime.DayOfWeek
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HistoryViewModel>()
    val state = viewModel.state.collectAsState()

    Box(modifier = modifier, contentAlignment = Center) {
        when (val it = state.value) {
            is HistoryState.Loaded -> LoadedHistory(
                history = it,
                onViewHistory = viewModel::onViewHistoryItem
            )

            HistoryState.Loading -> PulsingText("Loading History")
            HistoryState.Empty -> PulsingText("No history found")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoadedHistory(
    history: HistoryState.Loaded,
    onViewHistory: (HasHistory) -> Unit
) {
    log(tag = "HistoryScreen") { "Loaded history ${history.historyMonths.size}" }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { history.historyMonths.size })
    HorizontalPager(modifier = Modifier.fillMaxSize().padding(16.dp), state = pagerState) { page ->
        HistoryMonthView(month = history.historyMonths[page], onViewHistory = onViewHistory)
    }
}

@Composable
private fun HistoryMonthView(month: HistoryMonth, onViewHistory: (HasHistory) -> Unit) {
    HCard(horizontalAlignment = CenterHorizontally) {
        Text(text = month.title, style = MaterialTheme.typography.titleLarge)
        Spacer(height = 24.dp)
        HistoryCalendarView(days = month.list, onViewItem = onViewHistory)
    }
}

private const val UsedAlpha = .8f
private const val UnusedAlpha = .3f
private const val EmptyAlpha = .05f
private const val DaysPerWeek = 7

@Composable
private fun HistoryDay.backGroundColor() = Color.White.copy(alpha = backGroundAlpha())

@Composable
private fun HistoryDay.backGroundAlpha() = when (this) {
    is Future -> EmptyAlpha
    is HasHistory -> {
        val infiniteTransition = rememberInfiniteTransition(label = "Pulsing Text Transition")
        val alpha by infiniteTransition.animateFloat(
            initialValue = UnusedAlpha, targetValue = UsedAlpha,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600), repeatMode = RepeatMode.Reverse
            ),
            label = "Pulsing Text Alpha"
        )
        alpha
    }

    is Blank -> 0f
    is NoHistory -> UnusedAlpha
}

private fun HistoryDay.textColor() = Color.White.copy(alpha = textAlpha())

private fun HistoryDay.textAlpha() = when (this) {
    is Future -> EmptyAlpha
    is HasHistory -> UsedAlpha
    is Blank -> 0f
    is NoHistory -> UnusedAlpha
}

private val titles = DayOfWeek.entries.map { it.name.take(1) }

@Composable
private fun HistoryCalendarView(
    days: List<HistoryDay>,
    onViewItem: (HasHistory) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(DaysPerWeek),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(titles) {
            Box(modifier = Modifier.padding(4.dp), contentAlignment = Center) {
                Text(
                    text = it,
                    color = Color.White,
                )
            }
        }
        items(days) { day ->
            Box(
                modifier = Modifier.padding(4.dp).background(day.backGroundColor())
                    .conditional<HasHistory, _>(day) {
                        clickable { onViewItem(it) }
                    },
                contentAlignment = Center
            ) {
                Text(
                    text = "${day.dayOfMonth}", color = day.textColor()
                )
            }
        }
    }
}
