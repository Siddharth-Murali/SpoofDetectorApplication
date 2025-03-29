package com.traumatizedvitians.spoofdetectorapplication.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.traumatizedvitians.spoofdetectorapplication.ui.screens.FileAnalysisScreen
import com.traumatizedvitians.spoofdetectorapplication.ui.screens.HistoryScreen
import com.traumatizedvitians.spoofdetectorapplication.ui.screens.HomeScreen
import com.traumatizedvitians.spoofdetectorapplication.ui.screens.LiveDetectionScreen
import com.traumatizedvitians.spoofdetectorapplication.ui.screens.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }

        composable("live_detection") {
            LiveDetectionScreen(navController)
        }

        composable("file_analysis") {
            FileAnalysisScreen(navController)
        }

        composable("history") {
            HistoryScreen(navController)
        }

        composable("settings") {
            SettingsScreen(navController)
        }
    }
}
