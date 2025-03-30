package com.traumatizedvitians.spoofdetectorapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.traumatizedvitians.spoofdetectorapplication.detector.VoiceDetectorWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VoiceDetectionViewModel(application: Application) : AndroidViewModel(application) {
    private val detector = VoiceDetectorWrapper(application.applicationContext)

    private val _uiState = MutableStateFlow<VoiceDetectionState>(VoiceDetectionState.Initial)
    val uiState: StateFlow<VoiceDetectionState> = _uiState.asStateFlow()

    fun startDetection() {
        viewModelScope.launch {
            try {
                _uiState.value = VoiceDetectionState.Recording

                val result = detector.recordAndDetect()

                _uiState.value = VoiceDetectionState.Result(
                    isReal = result.isReal,
                    score = result.score,
                    prediction = result.prediction
                )
            } catch (e: Exception) {
                Log.e("VoiceDetectionVM", "Error during detection", e)
                _uiState.value = VoiceDetectionState.Error("Detection failed: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = VoiceDetectionState.Initial
    }

    sealed class VoiceDetectionState {
        object Initial : VoiceDetectionState()
        object Recording : VoiceDetectionState()
        data class Result(
            val isReal: Boolean,
            val score: Float,
            val prediction: String
        ) : VoiceDetectionState()
        data class Error(val message: String) : VoiceDetectionState()
    }
}
