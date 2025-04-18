package com.fergdev.hagah.screens.settings.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.layouts.Spacer
import com.fergdev.fcommon.ui.widgets.FiveWaySwipeableScreenScope
import com.fergdev.fcommon.ui.widgets.NumberIncrement
import com.fergdev.hagah.screens.settings.SettingsViewModel
import com.fergdev.hagah.ui.MinusIcon
import org.koin.compose.koinInject

@Composable
internal fun FiveWaySwipeableScreenScope.SettingsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val vm = koinInject<SettingsViewModel>()
        Column {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(height = 24.dp)

            val state by vm.state.collectAsState()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Meditation Length")
                Spacer(modifier = Modifier.weight(1f))
                NumberIncrement(
                    value = state.meditationDuration,
                    negativeIcon = MinusIcon,
                    positiveIcon = Icons.Default.Add,
                    formatter = { it.timeFormatted() }
                ) {
                    vm.setMeditationDuration(it)
                }
            }
        }
    }
}

private const val SECONDS_IN_MINUTE = 60L
private const val MINUTES_IN_HOUR = 60L
private const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
private const val SINGLE_DIGIT_MODULO = 10L
fun Long.timeFormatted(): String {
    if (this < 0L) throw IllegalStateException("Time cannot be negative")
    val hours = this / SECONDS_IN_HOUR
    val hoursString = if (hours == 0L) {
        ""
    } else {
        "$hours"
    }

    val minutes = this / MINUTES_IN_HOUR - hours * MINUTES_IN_HOUR
    val minutesString = if (minutes == 0L) {
        "00"
    } else if (minutes < SINGLE_DIGIT_MODULO) {
        "0$minutes"
    } else {
        "$minutes"
    }

    val seconds = this % SECONDS_IN_MINUTE
    val secondsString = if (seconds == 0L) {
        "00"
    } else if (seconds < SINGLE_DIGIT_MODULO) {
        "0$seconds"
    } else {
        "$seconds"
    }
    return if (hours == 0L) "$minutesString:$secondsString"
    else "$hoursString:$minutesString:$secondsString"
}
