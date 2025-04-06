package com.fergdev.dailydevotional.screens.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fergdev.dailydevotional.screens.settings.aboutlibs.AboutLibsContent
import com.fergdev.dailydevotional.ui.FIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    FIconButton(
                        onClick = { navController.popBackStack() },
                        vector = Icons.AutoMirrored.Filled.ArrowBack,
                    )
                },
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
            )
        }
    ) { padding ->
        val rememberNavController = rememberNavController()
        NavHost(navController = rememberNavController, startDestination = "about") {
            composable("main") { MainSettings() }
            composable("about") { AboutLibsContent() }
        }
    }
}

@Composable
fun MainSettings() {
    LazyColumn {
        item {
            Text("About")
        }
        item {
            Text("About libs")
        }
    }
}
