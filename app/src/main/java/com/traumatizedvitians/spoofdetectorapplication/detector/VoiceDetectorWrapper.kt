package com.traumatizedvitians.spoofdetectorapplication.detector

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VoiceDetectorWrapper(private val context: Context) {
    private val TAG = "VoiceDetectorWrapper"
    private var detector: PyObject? = null
    private val sampleRate = 16000
    
    init {
        initializePython()
    }

    private fun initializePython() {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }

        try {
            // Copy model and config files from assets to app's files directory
            copyAssetToFile("samo.pt", "samo.pt")
            copyAssetToFile("AASIST.conf", "AASIST.conf")

            // Get Python module
            val py = Python.getInstance()
            val module = py.getModule("voice_detector")

            // Create detector instance
            val modelPath = File(context.filesDir, "samo.pt").absolutePath
            val configPath = File(context.filesDir, "AASIST.conf").absolutePath
            detector = module.callAttr("VoiceDetector", modelPath, configPath)

            Log.d(TAG, "Python voice detector initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Python voice detector", e)
        }
    }

    private fun copyAssetToFile(assetName: String, fileName: String) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            context.assets.open(assetName).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    suspend fun recordAndDetect(durationMs: Int = 5000): DetectionResult = withContext(Dispatchers.IO) {
        // Check for microphone permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            return@withContext DetectionResult(
                isReal = false,
                score = 0.0f,
                prediction = "Error: Microphone permission not granted"
            )
        }
        
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid buffer size: $bufferSize")
            return@withContext DetectionResult(
                isReal = false,
                score = 0.0f,
                prediction = "Error: Device doesn't support required audio format"
            )
        }

        var audioRecord: AudioRecord? = null
        val samples = ShortArray((sampleRate * (durationMs / 1000.0)).toInt())
        var result = DetectionResult(isReal = false, score = 0.0f, prediction = "Error during detection")

        try {
            // Create AudioRecord with a larger buffer to avoid underruns
            val actualBufferSize = bufferSize * 2
            
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                actualBufferSize
            )

            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                val errorMessage = when (audioRecord.state) {
                    AudioRecord.STATE_UNINITIALIZED -> "Audio recorder uninitialized"
                    else -> "Unknown audio recorder state: ${audioRecord.state}"
                }
                Log.e(TAG, "AudioRecord failed to initialize: $errorMessage")
                return@withContext DetectionResult(
                    isReal = false,
                    score = 0.0f,
                    prediction = "Error: Could not initialize audio recording. Please check microphone access and try again."
                )
            }

            audioRecord.startRecording()
            Log.d(TAG, "Recording started...")

            // Read audio data with timeout handling
            var readSize = 0
            var offset = 0
            val timeout = System.currentTimeMillis() + durationMs + 1000 // Add 1 second buffer
            
            while (offset < samples.size && System.currentTimeMillis() < timeout) {
                readSize = audioRecord.read(samples, offset, samples.size - offset)
                if (readSize > 0) {
                    offset += readSize
                } else {
                    Log.w(TAG, "Audio read returned $readSize")
                    break
                }
            }

            Log.d(TAG, "Recording finished, read $offset samples")
            
            if (offset <= 0) {
                return@withContext DetectionResult(
                    isReal = false,
                    score = 0.0f,
                    prediction = "Error: No audio data recorded. Please check your microphone."
                )
            }

            // Convert to float array for Python
            val floatSamples = FloatArray(samples.size)
            for (i in samples.indices) {
                floatSamples[i] = samples[i] / 32768.0f
            }

            // Call Python detector
            val score = detector?.callAttr("predict", floatSamples)?.toJava(Float::class.java) ?: 0.0f

            val isReal = score > 0
            result = DetectionResult(
                isReal = isReal,
                score = score,
                prediction = if (isReal) "Real (Bonafide)" else "Fake (Spoof)"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during voice detection", e)
            result = DetectionResult(
                isReal = false,
                score = 0.0f,
                prediction = "Error: ${e.message ?: "Unknown error during recording"}"
            )
        } finally {
            try {
                if (audioRecord != null) {
                    if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                        audioRecord.stop()
                    }
                    audioRecord.release()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping AudioRecord", e)
            }
        }
        
        return@withContext result
    }

    data class DetectionResult(
        val isReal: Boolean,
        val score: Float,
        val prediction: String
    )
}
