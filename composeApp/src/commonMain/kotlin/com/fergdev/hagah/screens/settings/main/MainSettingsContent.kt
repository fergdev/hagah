package com.fergdev.hagah.screens.settings.main

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fergdev.hagah.ui.FIconButton
import com.fergdev.hagah.ui.FTopAppBar
import hagah.generated.resources.Res
import hagah.generated.resources.settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainSettingsContent(
    onBack: () -> Unit,
    onAbout: () -> Unit,
) {
    Scaffold(
        topBar = {
            FTopAppBar(
                title = stringResource(Res.string.settings),
                navigationIcon = {
                    FIconButton(
                        onClick = onBack,
                        vector = Icons.AutoMirrored.Filled.ArrowBack,
                    )
                },
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            item {
                Button(onClick = onAbout) {
                    Text("About")
                }
            }
            item {
            }
        }
    }
}
