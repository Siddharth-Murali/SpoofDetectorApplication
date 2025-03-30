package com.traumatizedvitians.spoofdetectorapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.traumatizedvitians.spoofdetectorapplication.viewmodel.VoiceDetectionViewModel
import androidx.compose.material.icons.filled.Mic

@Composable
fun VoiceDetectionScreen(
    viewModel: VoiceDetectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Voice Spoof Detection",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (uiState) {
            is VoiceDetectionViewModel.VoiceDetectionState.Initial -> {
                InitialState(onStartDetection = { viewModel.startDetection() })
            }

            is VoiceDetectionViewModel.VoiceDetectionState.Recording -> {
                RecordingState()
            }

            is VoiceDetectionViewModel.VoiceDetectionState.Result -> {
                val result = uiState as VoiceDetectionViewModel.VoiceDetectionState.Result
                ResultState(
                    result = result,
                    onReset = { viewModel.resetState() }
                )
            }

            is VoiceDetectionViewModel.VoiceDetectionState.Error -> {
                val error = uiState as VoiceDetectionViewModel.VoiceDetectionState.Error
                ErrorState(
                    message = error.message,
                    onReset = { viewModel.resetState() }
                )
            }
        }
    }
}

@Composable
fun InitialState(onStartDetection: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Press the button below to start voice detection",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onStartDetection,
            modifier = Modifier.size(120.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Start Recording",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun RecordingState() {
    val infiniteTransition = rememberInfiniteTransition(label = "recording_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recording...",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Recording",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please speak now...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ResultState(
    result: VoiceDetectionViewModel.VoiceDetectionState.Result,
    onReset: () -> Unit
) {
    val backgroundColor = if (result.isReal) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
    }

    val textColor = if (result.isReal) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = result.prediction,
                style = MaterialTheme.typography.headlineSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Score: ${result.score.format(2)}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (result.isReal) {
                    "The voice appears to be from a real person."
                } else {
                    "The voice appears to be artificially generated or manipulated."
                },
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(onClick = onReset) {
        Text("Try Again")
    }
}

@Composable
fun ErrorState(
    message: String,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(onClick = onReset) {
        Text("Try Again")
    }
}

// Extension function to format Float with specific decimal places
private fun Float.format(digits: Int): String = "%.${digits}f".format(this)
