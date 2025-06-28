package com.mercu.intrain.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ChatConResponse
import com.mercu.intrain.API.ChatContinous
import com.mercu.intrain.API.ChatResponse
import com.mercu.intrain.API.Evaluation
import com.mercu.intrain.API.EvaluationResponse
import com.mercu.intrain.model.EndFail
import com.mercu.intrain.model.Feedback
import com.mercu.intrain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(sessionId: String, userMessage: String, onEval: (Evaluation) -> Unit) {
        appendMessage(Message(userMessage, isUser = true))
        _isLoading.value = true

        val chatRequest = ChatContinous(sessionId, userMessage)
        ApiConfig.api.chatRequestContinous(chatRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    val json = response.body()!!.string()
                    val gson = Gson()
                    try {
                        // 👉 1. Check if response is Feedback
                        val feedbackResponse = gson.fromJson(json, Feedback::class.java)
                        if (feedbackResponse.response?.feedbackText != null) {
                            appendMessage(Message("📩 ${feedbackResponse.response.feedbackText}", isUser = false))
                            return
                        }

                        // 👉 2. Check if response is a normal Chat message
                        val chatResponse = gson.fromJson(json, ChatConResponse::class.java)
                        chatResponse.response?.questionText?.let {
                            appendMessage(Message("🤖 $it", isUser = false))
                            return
                        }


                        // 👉 3. Check if response is Evaluation
                        val evalResponse = gson.fromJson(json, EvaluationResponse::class.java)
                        evalResponse.evaluation?.let {
                            onEval(it)
                            return
                        }

                        appendMessage(Message("❌ Format tidak dikenali", false))

                    } catch (e: Exception) {
                        appendMessage(Message("❌ Parsing error: ${e.message}", false))
                    }
                } else {
                    appendMessage(Message("❌ Server error (${response.code()})", false))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _isLoading.value = false
                appendMessage(Message("❌ Gagal konek: ${t.message}", false))
            }
        })
    }


    fun initializeFirstMessage(chatResponse: ChatResponse?) {
        chatResponse?.questionText?.let { text ->
            _messages.value = listOf(Message("🤖 $text", isUser = false))
        }
    }



    private fun appendMessage(msg: Message) {
        _messages.value = _messages.value + msg
    }
}
