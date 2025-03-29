package com.traumatizedvitians.spoofdetectorapplication.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.DangerColor
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.SafeColor
import com.traumatizedvitians.spoofdetectorapplication.ui.theme.WarningColor

@Composable
fun DetectionResultCard(
    spoofScore: Double,
    isLive: Boolean,
    onReportClick: () -> Unit = {}
) {
    val resultColor = when {
        spoofScore < 0.3 -> SafeColor
        spoofScore < 0.7 -> WarningColor
        else -> DangerColor
    }

    val resultTitle = when {
        spoofScore < 0.3 -> "Likely Authentic"
        spoofScore < 0.7 -> "Potentially Spoofed"
        else -> "Likely Fake Voice"
    }

    val resultDescription = when {
        spoofScore < 0.3 -> "This voice appears to be authentic with natural speech patterns and acoustic properties."
        spoofScore < 0.7 -> "Some unusual patterns detected. Be cautious and verify the caller's identity through other means."
        else -> "High probability of synthetic or manipulated voice. Do not share sensitive information!"
    }

    val resultIcon = when {
        spoofScore < 0.3 -> Icons.Filled.Check
        spoofScore < 0.7 -> Icons.Filled.Warning
        else -> Icons.Filled.Close
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = resultColor.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Icon(
                    imageVector = resultIcon,
                    contentDescription = resultTitle,
                    tint = resultColor,
                    modifier = Modifier.padding(end = 12.dp)
                )

                Column {
                    Text(
                        text = resultTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = resultColor
                    )

                    Text(
                        text = if (isLive) "Live Voice Analysis" else "Audio File Analysis",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            Text(
                text = resultDescription,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (spoofScore >= 0.7) {
                OutlinedButton(
                    onClick = onReportClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Report",
                        tint = DangerColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Report Suspicious Voice",
                        color = DangerColor
                    )
                }
            }
        }
    }
}
