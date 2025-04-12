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
import androidx.compose.foundation.layout.Column
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
import com.fergdev.fcommon.ui.Spacer
import com.fergdev.fcommon.ui.conditional
import com.fergdev.hagah.ui.PulsingText
import kotlinx.datetime.DayOfWeek
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HistoryViewModel>()
    val state = viewModel.state.collectAsState()

    Box(modifier = modifier, contentAlignment = Center) {
        when (state.value) {
            is HistoryState.Loaded -> LoadedHistory(
                history = state.value as HistoryState.Loaded,
                onViewHistory = viewModel::onViewHistory
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
    onViewHistory: (HistoryDay.HasHistory) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { history.historyMonths.size - 1 }
    )
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState
    ) { page ->
        Box(contentAlignment = Center) {
            HistoryMonthView(month = history.historyMonths[page], onViewHistory = onViewHistory)
        }
    }
}

@Composable
private fun HistoryMonthView(month: HistoryMonth, onViewHistory: (HistoryDay.HasHistory) -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = CenterHorizontally
        ) {
            Text(
                text = month.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(height = 24.dp)
            HistoryCalendarView(days = month.list, onViewItem = onViewHistory)
        }
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
    is HistoryDay.Future -> EmptyAlpha
    is HistoryDay.HasHistory -> {
        val infiniteTransition = rememberInfiniteTransition(label = "Pulsing Text Transition")
        val alpha by infiniteTransition.animateFloat(
            initialValue = UnusedAlpha,
            targetValue = UsedAlpha,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Pulsing Text Alpha"
        )
        alpha
    }

    is HistoryDay.Blank -> 0f
    is HistoryDay.NoHistory -> UnusedAlpha
}

private fun HistoryDay.textColor() = Color.White.copy(alpha = textAlpha())

private fun HistoryDay.textAlpha() = when (this) {
    is HistoryDay.Future -> EmptyAlpha
    is HistoryDay.HasHistory -> UsedAlpha
    is HistoryDay.Blank -> 0f
    is HistoryDay.NoHistory -> UnusedAlpha
}

private val titles = DayOfWeek.entries.map { it.name.take(1) }

@Composable
private fun HistoryCalendarView(
    days: List<HistoryDay>,
    onViewItem: (HistoryDay.HasHistory) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(DaysPerWeek),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(titles) {
            Box(
                modifier = Modifier.padding(4.dp),
                contentAlignment = Center
            ) {
                Text(
                    text = it,
                    color = Color.White,
                )
            }
        }
        items(days) { day ->
            Box(
                modifier = Modifier.padding(4.dp)
                    .background(day.backGroundColor())
                    .conditional<HistoryDay.HasHistory, _>(day) {
                        clickable { onViewItem(it) }
                    },
                contentAlignment = Center
            ) {
                Text(
                    text = "${day.dayOfMonth}",
                    color = day.textColor()
                )
            }
        }
    }
}
