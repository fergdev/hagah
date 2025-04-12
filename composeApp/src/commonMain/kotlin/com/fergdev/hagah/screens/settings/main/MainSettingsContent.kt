package com.fergdev.hagah.screens.settings.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.Spacer
import com.fergdev.fcommon.ui.widgets.FiveWaySwipeableScreenScope
import com.fergdev.fcommon.ui.widgets.NumberIncrement
import com.fergdev.hagah.screens.settings.SettingsViewModel
import org.koin.compose.koinInject

@Composable
internal fun FiveWaySwipeableScreenScope.SettingsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val vm = koinInject<SettingsViewModel>()
        Column {
            Text(text = "Settings")

            val state by vm.state.collectAsState()
            Row {
                Text(text = "Meditation Length")
                Spacer(modifier = Modifier.weight(1f))
                NumberIncrement(
                    value = state.meditationDuration,
                    negativeIcon = Icons.Default.Delete,
                    positiveIcon = Icons.Default.Add,
                ) {
                    vm.setMeditationDuration(it)
                }
            }
        }
    }
}
