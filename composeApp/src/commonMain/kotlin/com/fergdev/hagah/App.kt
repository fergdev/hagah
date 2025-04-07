package com.fergdev.hagah

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fergdev.hagah.screens.main.MainScreen
import com.fergdev.hagah.screens.settings.SettingsScreen
import com.fergdev.hagah.screens.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        Surface {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main") {
                composable("main") { MainScreen(navController) }
                composable("settings") {
                    SettingsScreen {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
