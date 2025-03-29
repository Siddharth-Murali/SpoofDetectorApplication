package com.traumatizedvitians.spoofdetectorapplication.model

import java.util.Date

data class HistoryItem(
    val id: String,
    val spoofScore: Double,
    val timestamp: Date,
    val source: String,
    val isLive: Boolean
)
