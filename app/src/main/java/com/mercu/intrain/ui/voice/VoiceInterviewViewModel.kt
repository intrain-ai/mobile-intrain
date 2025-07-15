package com.mercu.intrain.ui.voice

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

sealed class VoiceInterviewState {
    object Idle : VoiceInterviewState()
    object Loading : VoiceInterviewState()
    data class InitSuccess(val response: VoiceInterviewInitResponse) : VoiceInterviewState()
    data class ContinueSuccess(val response: VoiceInterviewContinueResponse) : VoiceInterviewState()
    data class Error(val message: String) : VoiceInterviewState()
}

class VoiceInterviewViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow<VoiceInterviewState>(VoiceInterviewState.Idle)
    val state: StateFlow<VoiceInterviewState> = _state

    fun initInterview(userId: String, hrLevelId: Int, jobType: String) {
        _state.value = VoiceInterviewState.Loading
        viewModelScope.launch {
            try {
                val req = VoiceInterviewInitRequest(userId, hrLevelId, jobType)
                val resp = ApiConfig.api.initVoiceInterview(req)
                if (resp.isSuccessful && resp.body() != null) {
                    _state.value = VoiceInterviewState.InitSuccess(resp.body()!!)
                } else {
                    val errorMsg = when (resp.code()) {
                        500 -> "Terjadi kesalahan pada server (500). Silakan coba lagi nanti."
                        408 -> "Koneksi ke server terlalu lama (Timeout). Silakan periksa koneksi internet Anda."
                        else -> "Init failed: ${resp.code()}"
                    }
                    _state.value = VoiceInterviewState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("timeout", ignoreCase = true) == true -> "Koneksi ke server terlalu lama (Timeout). Silakan periksa koneksi internet Anda."
                    e.message?.contains("failed to connect", ignoreCase = true) == true -> "Tidak dapat terhubung ke server. Silakan periksa koneksi internet Anda."
                    else -> "Init error: ${e.localizedMessage}"
                }
                _state.value = VoiceInterviewState.Error(msg)
            }
        }
    }

    fun continueInterview(sessionId: String, audioFile: File) {
        _state.value = VoiceInterviewState.Loading
        viewModelScope.launch {
            try {
                val reqFile = audioFile.asRequestBody("audio/mpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", audioFile.name, reqFile)
                val sessionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), sessionId)
                val resp = ApiConfig.api.continueVoiceInterview(part, sessionBody)
                if (resp.isSuccessful && resp.body() != null) {
                    _state.value = VoiceInterviewState.ContinueSuccess(resp.body()!!)
                } else {
                    val errorMsg = try {
                        val errorBody = resp.errorBody()?.string()
                        if (!errorBody.isNullOrBlank()) {
                            JSONObject(errorBody).optString("error")
                        } else null
                    } catch (e: Exception) { null }
                    val friendlyMsg = when (resp.code()) {
                        500 -> "Terjadi kesalahan pada server (500). Silakan coba lagi nanti."
                        408 -> "Koneksi ke server terlalu lama (Timeout). Silakan periksa koneksi internet Anda."
                        else -> errorMsg ?: "Terjadi kesalahan pada server. Silakan coba lagi nanti."
                    }
                    _state.value = VoiceInterviewState.Error(friendlyMsg)
                }
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("timeout", ignoreCase = true) == true -> "Koneksi ke server terlalu lama (Timeout). Silakan periksa koneksi internet Anda."
                    e.message?.contains("failed to connect", ignoreCase = true) == true -> "Tidak dapat terhubung ke server. Silakan periksa koneksi internet Anda."
                    else -> "Gagal mengirim jawaban: ${e.localizedMessage}"
                }
                _state.value = VoiceInterviewState.Error(msg)
            }
        }
    }

    fun reset() {
        _state.value = VoiceInterviewState.Idle
    }
} 