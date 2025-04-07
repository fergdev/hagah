package com.fergdev.hagah.screens.settings.about.aboutMain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.fergdev.hagah.screens.settings.about.aboutlibs.AboutLibsContent
import com.fergdev.hagah.ui.FIconButton
import com.fergdev.hagah.ui.FTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutMainContent(onBack: () -> Unit) {
    var aboutLibsVisible by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            FTopAppBar(
                title = "About",
                actions = {
                    FIconButton(
                        onClick = onBack,
                        vector = Icons.AutoMirrored.Filled.ArrowBack,
                    )
                }
            )
        }
    ) {


    }

    if (aboutLibsVisible) {
        ModalBottomSheet(onDismissRequest = { aboutLibsVisible = false }) {
            AboutLibsContent()
        }
    }
}
