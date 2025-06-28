package com.mercu.intrain.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ChatInitResponse
import com.mercu.intrain.API.ChatRequest
import com.mercu.intrain.API.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DiffSelectViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    fun initializeChat(
        userId: String,
        hrLevel: Int,
        jobType: String,
        onSuccess: (sessionId: String, response: ChatResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading = true
        val request = ChatRequest(jobType, userId, hrLevel)
        ApiConfig.api.chatRequestInit(request).enqueue(object : Callback<ChatInitResponse> {
            override fun onResponse(call: Call<ChatInitResponse>, response: Response<ChatInitResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.response != null) {
                        onSuccess(body.sessionId ?: "", body.response)
                    } else {
                        onError("Response tidak valid.")
                    }
                } else {
                    onError("Server Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ChatInitResponse>, t: Throwable) {
                isLoading = false
                onError("Gagal koneksi: ${t.message}")
            }
        })
    }
}
