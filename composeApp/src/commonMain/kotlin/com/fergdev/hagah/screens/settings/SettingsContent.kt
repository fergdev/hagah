package com.fergdev.hagah.screens.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fergdev.hagah.screens.settings.about.AboutNav
import com.fergdev.hagah.screens.settings.main.MainSettingsContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainSettingsContent(
                onBack = onBack,
                onAbout = { navController.popBackStack() }
            )
        }
        composable("about") {
            AboutNav(onBack = { navController.popBackStack() })
        }
    }
}
