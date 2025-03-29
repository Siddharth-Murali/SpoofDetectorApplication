package com.traumatizedvitians.spoofdetectorapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.traumatizedvitians.spoofdetectorapplication.ui.components.FeatureCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spoof Detector") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Feature cards
            androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)

            FeatureCard(
                title = "Live Call Detection",
                description = "Detect spoofing in real-time calls",
                icon = Icons.Filled.Phone,  // Changed from PhoneInTalk to Phone
                onClick = { navController.navigate("live_detection") }
            )

            FeatureCard(
                title = "Analyze Audio File",
                description = "Check audio files for spoofing",
                icon = Icons.Filled.Add,  // Changed from UploadFile to Add
                onClick = { navController.navigate("file_analysis") }
            )

            FeatureCard(
                title = "Detection History",
                description = "View past detection results",
                icon = Icons.Filled.Info,  // Changed from History to Info
                onClick = { navController.navigate("history") }
            )

            FeatureCard(
                title = "Settings",
                description = "Configure app preferences",
                icon = Icons.Filled.Settings,
                onClick = { navController.navigate("settings") }
            )
        }
    }
}
