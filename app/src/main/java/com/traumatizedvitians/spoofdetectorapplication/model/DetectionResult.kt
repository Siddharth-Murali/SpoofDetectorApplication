package com.traumatizedvitians.spoofdetectorapplication.model

data class DetectionResult(
    val spoofScore: Double,
    val details: Map<String, Any>? = null
)
