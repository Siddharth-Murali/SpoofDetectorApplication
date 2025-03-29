package com.traumatizedvitians.spoofdetectorapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.DangerColor
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.SafeColor
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.WarningColor

@Composable
fun SpoofRiskMeter(spoofScore: Double) {
    val riskColor = when {
        spoofScore < 0.3 -> SafeColor
        spoofScore < 0.7 -> WarningColor
        else -> DangerColor
    }

    val riskText = when {
        spoofScore < 0.3 -> "Low Risk"
        spoofScore < 0.7 -> "Medium Risk"
        else -> "High Risk"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Spoof Risk: ${(spoofScore * 100).toInt()}%",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = riskColor
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { spoofScore.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            color = riskColor,
            trackColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.2f)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = riskText,
            fontSize = 16.sp,
            color = riskColor
        )
    }
}
