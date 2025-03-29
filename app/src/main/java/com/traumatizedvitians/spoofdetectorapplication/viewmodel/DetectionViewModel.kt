package com.traumatizedvitians.spoofdetectorapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.traumatizedvitians.spoofdetectorapplication.model.DetectionResult
import com.traumatizedvitians.spoofdetectorapplication.model.HistoryItem
import com.traumatizedvitians.spoofdetectorapplication.service.DetectionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class DetectionViewModel(application: Application) : AndroidViewModel(application) {

    private val detectionService = DetectionService(application.applicationContext)

    // Detection state
    private val _detectionState = MutableStateFlow<DetectionState>(DetectionState.Initial)
    val detectionState: StateFlow<DetectionState> = _detectionState

    // History items
    private val _historyItems = MutableStateFlow<List<HistoryItem>>(emptyList())
    val historyItems: StateFlow<List<HistoryItem>> = _historyItems

    // Detection states
    sealed class DetectionState {
        object Initial : DetectionState()
        object Detecting : DetectionState()
        data class Completed(val result: DetectionResult) : DetectionState()
        data class Error(val message: String) : DetectionState()
    }

    // Start live detection
    fun startLiveDetection() {
        _detectionState.value = DetectionState.Detecting

        viewModelScope.launch {
            try {
                val result = detectionService.analyzeLiveAudio()

                // Update state
                _detectionState.value = DetectionState.Completed(result)

                // Add to history
                addToHistory(
                    HistoryItem(
                        id = System.currentTimeMillis().toString(),
                        spoofScore = result.spoofScore,
                        timestamp = Date(),
                        source = "Live Call Detection",
                        isLive = true
                    )
                )

            } catch (e: Exception) {
                _detectionState.value = DetectionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Stop live detection
    fun stopLiveDetection() {
        if (_detectionState.value is DetectionState.Detecting) {
            _detectionState.value = DetectionState.Initial
        }
    }

    // Analyze audio file
    fun analyzeAudioFile(filePath: String) {
        _detectionState.value = DetectionState.Detecting

        viewModelScope.launch {
            try {
                val result = detectionService.analyzeAudioFile(filePath)

                // Update state
                _detectionState.value = DetectionState.Completed(result)

                // Add to history
                addToHistory(
                    HistoryItem(
                        id = System.currentTimeMillis().toString(),
                        spoofScore = result.spoofScore,
                        timestamp = Date(),
                        source = "File: ${filePath.substringAfterLast("/")}",
                        isLive = false
                    )
                )

            } catch (e: Exception) {
                _detectionState.value = DetectionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Add item to history
    private fun addToHistory(item: HistoryItem) {
        val currentList = _historyItems.value.toMutableList()
        currentList.add(0, item) // Add to beginning of list
        _historyItems.value = currentList
    }
}
