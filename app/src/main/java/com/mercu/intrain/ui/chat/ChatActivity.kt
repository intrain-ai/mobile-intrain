package com.mercu.intrain.ui.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionId = intent.getStringExtra("session_id") ?: ""
        val chatResponse = intent.getParcelableExtra<ChatResponse>("chat_response")

        setContent {
            MaterialTheme {
                if (chatResponse != null) {
                    val context = LocalContext.current
                    ChatScreen(
                        sessionId = sessionId,
                        chatResponse = chatResponse,
                        onNavigateToEvaluation = { evaluation ->
                            val intent = Intent(context, EvaluationActivity::class.java)
                            intent.putExtra("evaluation_data", evaluation)
                            context.startActivity(intent)
                            if (context is Activity) context.finish()
                        }
                    )
                }
            }
        }
    }
}
