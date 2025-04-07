package com.fergdev.hagah.screens.settings.about

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fergdev.hagah.screens.settings.about.aboutMain.AboutMainContent

@Composable
fun AboutNav(onBack: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            AboutMainContent(onBack)
        }
        composable("aboutVersion") {

        }
    }
}