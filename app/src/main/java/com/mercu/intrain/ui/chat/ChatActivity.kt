package com.mercu.intrain.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.protobuf.Value
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ChatConResponse
import com.mercu.intrain.API.ChatContinous
import com.mercu.intrain.API.ChatInitResponse
import com.mercu.intrain.API.ChatResponse
import com.mercu.intrain.API.EvaluationResponse
import com.mercu.intrain.R
import com.mercu.intrain.model.Message
import com.mercu.intrain.ui.evaluation.EvaluationActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var adapter: ChatAdapter


    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.recyclerView)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val sessionId = intent.getStringExtra("session_id")

        val chatResponse = intent.getParcelableExtra<ChatResponse>("chat_response")

        if (chatResponse != null) {
            messages.add(Message("🤖 ${chatResponse.questionText}", false))
            adapter.notifyItemInserted(messages.size - 1)
        } else {
            showToast("No chat response data received.")
        }

        buttonSend.setOnClickListener {
            val text = editTextMessage.text.toString()
            if (text.isNotBlank()) {
                messages.add(Message(text, true))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)

                sessionId?.let {
                    chatRequestCon(it, text)
                } ?: showToast("Session ID kosong")

                editTextMessage.text.clear()
            }
        }

        showToast("Session ID: $sessionId")
    }

    private fun chatRequestCon(sessionId: String, message: String) {
        val chatBot = ChatContinous(sessionId, message)

        ApiConfig.api.chatRequestContinous(chatBot).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    val json = response.body()!!.string()
                    val gson = Gson()



                    try {
                        val questionObj = gson.fromJson(json, ChatConResponse::class.java)
                        val questionNumber = questionObj.response?.questionNumber
                        val questionText = questionObj.response?.questionText

                        if (questionNumber != null && questionText != null) {
                            messages.add(Message("🤖 $questionText", false))
                            adapter.notifyItemInserted(messages.size - 1)
                            recyclerView.scrollToPosition(messages.size - 1)
                            return
                        }

                        val evalObj = gson.fromJson(json, EvaluationResponse::class.java)
                        val evaluation = evalObj.evaluation

                        if (evaluation != null) {
                            val intent = Intent(this@ChatActivity, EvaluationActivity::class.java)
                            intent.putExtra("evaluation_data", evaluation)
                            startActivity(intent)
                            finish()
                            return
                        }

                        // 3. Kalau dua-duanya gagal
                        messages.add(Message("❌ Format tidak dikenali dari server", false))
                        adapter.notifyItemInserted(messages.size - 1)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        messages.add(Message("❌ Parsing error: ${e.message}", false))
                        adapter.notifyItemInserted(messages.size - 1)
                    }

                } else {
                    messages.add(Message("❌ Server error (${response.code()})", false))
                    adapter.notifyItemInserted(messages.size - 1)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                messages.add(Message("❌ Gagal konek: ${t.message}", false))
                adapter.notifyItemInserted(messages.size - 1)
            }
        })
    }




    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
