package com.traumatizedvitians.spoofdetectorapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.traumatizedvitians.spoofdetectorapplication.ui.components.DetectionResultCard
import com.traumatizedvitians.spoofdetectorapplication.ui.components.SpoofRiskMeter
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.DangerColor
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.PrimaryColor
import com.traumatizedvitians.spoofdetectorapplication.viewmodel.DetectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDetectionScreen(
    navController: NavController,
    viewModel: DetectionViewModel = viewModel()
) {

    val detectionState by viewModel.detectionState.collectAsState()
    val isDetecting = detectionState is DetectionViewModel.DetectionState.Detecting

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Call Detection") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDetecting) PrimaryColor else Color.Gray)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isDetecting) "LIVE DETECTION ACTIVE" else "DETECTION INACTIVE",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animation placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isDetecting) "Analyzing voice..." else "Press start to begin detection",
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Results
                when (detectionState) {
                    is DetectionViewModel.DetectionState.Completed -> {
                        val result = (detectionState as DetectionViewModel.DetectionState.Completed).result
                        SpoofRiskMeter(spoofScore = result.spoofScore)

                        Spacer(modifier = Modifier.height(24.dp))

                        DetectionResultCard(
                            spoofScore = result.spoofScore,
                            isLive = true
                        )
                    }

                    is DetectionViewModel.DetectionState.Error -> {
                        Text(
                            text = "Error: ${(detectionState as DetectionViewModel.DetectionState.Error).message}",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    else -> {
                        // Show nothing or a placeholder
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Control button
                Button(
                    onClick = {
                        if (isDetecting) {
                            viewModel.stopLiveDetection()
                        } else {
                            viewModel.startLiveDetection()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDetecting) DangerColor else PrimaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = if (isDetecting) Icons.Filled.Close else Icons.Filled.Check,
                        contentDescription = if (isDetecting) "Stop Detection" else "Start Detection"
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(
                        text = if (isDetecting) "Stop Detection" else "Start Detection"
                    )
                }
            }
        }
    }
}
