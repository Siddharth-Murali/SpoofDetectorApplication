package com.traumatizedvitians.spoofdetectorapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.traumatizedvitians.spoofdetectorapplication.navigation.AppNavigation
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.SpoofDetectorApplicationTheme
import com.traumatizedvitians.spoofdetectorapplication.viewmodel.DetectionViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DetectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[DetectionViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            SpoofDetectorApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
