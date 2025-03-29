package com.traumatizedvitians.spoofdetectorapplication.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.traumatizedvitians.spoofdetectorapplication.ui.components.DetectionResultCard
import com.traumatizedvitians.spoofdetectorapplication.ui.components.SpoofRiskMeter
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.PrimaryColor
import com.traumatizedvitians.spoofdetectorapplication.viewmodel.DetectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileAnalysisScreen(
    navController: NavController,
    viewModel: DetectionViewModel = viewModel()
) {
    val detectionState by viewModel.detectionState.collectAsState()
    val context = LocalContext.current
    var selectedAudioFile by remember { mutableStateOf<Uri?>(null) }

    val audioFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedAudioFile = it
            // Get the file path from URI
            val filePath = uri.path ?: return@let
            viewModel.analyzeAudioFile(filePath)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analyze Audio File") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Upload area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        audioFilePicker.launch("audio/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Upload",
                        tint = PrimaryColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = if (detectionState is DetectionViewModel.DetectionState.Detecting)
                            "Analyzing..."
                        else
                            "Tap to upload audio file",
                        color = PrimaryColor
                    )

                    if (selectedAudioFile != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "File: ${selectedAudioFile.toString().substringAfterLast("/")}",
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Loading or results
            when (detectionState) {
                is DetectionViewModel.DetectionState.Detecting -> {
                    // Loading indicator
                    androidx.compose.material3.CircularProgressIndicator(
                        color = PrimaryColor
                    )
                    Text(
                        text = "Analyzing audio...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                is DetectionViewModel.DetectionState.Completed -> {
                    val result = (detectionState as DetectionViewModel.DetectionState.Completed).result
                    SpoofRiskMeter(spoofScore = result.spoofScore)

                    Spacer(modifier = Modifier.height(24.dp))

                    DetectionResultCard(
                        spoofScore = result.spoofScore,
                        isLive = false
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ElevatedButton(
                        onClick = { audioFilePicker.launch("audio/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Analyze Another File"
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Text("Analyze Another File")
                    }
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
                    // Initial state - show upload prompt
                    Text(
                        text = "Upload an audio file to analyze it for spoofing",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        }
    }
}

