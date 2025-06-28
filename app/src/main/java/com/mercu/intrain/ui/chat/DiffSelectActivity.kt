package com.mercu.intrain.ui.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mercu.intrain.ui.chat.ChatActivity
import com.mercu.intrain.ui.chat.DiffSelectScreen


class DiffSelectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    DiffSelectScreen(
                        navToChat = { sessionId, chatResponse, hrLevel ->
                            val intent = Intent(this, ChatActivity::class.java).apply {
                                putExtra("session_id", sessionId)
                                putExtra("chat_response", chatResponse)
                                putExtra("hr_level", hrLevel)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
