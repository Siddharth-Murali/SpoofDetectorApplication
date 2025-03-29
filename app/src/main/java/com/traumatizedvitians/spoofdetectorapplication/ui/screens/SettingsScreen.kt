package com.traumatizedvitians.spoofdetectorapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var detectionSensitivity by remember { mutableStateOf(0.5f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Appearance section
            SectionHeader(title = "Appearance")

            SettingItem(
                title = "Dark Mode",
                subtitle = "Switch between light and dark theme",
                trailing = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { isDarkMode = it }
                    )
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Notifications section
            SectionHeader(title = "Notifications")

            SettingItem(
                title = "Push Notifications",
                subtitle = "Receive alerts for high-risk detections",
                trailing = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Detection Settings section
            SectionHeader(title = "Detection Settings")

            SettingItem(
                title = "Detection Sensitivity",
                subtitle = "Adjust how sensitive the detection is"
            )

            Slider(
                value = detectionSensitivity,
                onValueChange = { detectionSensitivity = it },
                valueRange = 0f..1f,
                steps = 10,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Text("Less Sensitive", color = Color.Gray)
                Text("More Sensitive", color = Color.Gray)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // About section
            SectionHeader(title = "About")

            SettingItem(
                title = "Version",
                subtitle = "1.0.0"
            )

            SettingItem(
                title = "Privacy Policy",
                subtitle = "Read how we protect your data",
                onClick = {
                    // Open privacy policy
                }
            )

            SettingItem(
                title = "Terms of Service",
                subtitle = "Read our terms and conditions",
                onClick = {
                    // Open terms of service
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = PrimaryColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        trailing?.invoke()
    }
}
