package com.mercu.intrain.ui.chat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.chat.ChatActivity
import com.mercu.intrain.ui.chat.DiffSelectScreen
import com.mercu.intrain.ui.voice.VoiceInterviewActivity

lateinit var sharedPrefHelper: SharedPrefHelper

class DiffSelectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefHelper = SharedPrefHelper(this)
        val jobType = sharedPrefHelper.getJobType()
        val userId = sharedPrefHelper.getUid()

        setContent {
            MaterialTheme {
                Surface {
                    DiffSelectScreen(
                        navToChat = { sessionId, chatResponse, hrLevel , interviewType->
                          if (interviewType == 0){
                              val intent = Intent(this, ChatActivity::class.java)
                              intent.putExtra("session_id", sessionId)
                              intent.putExtra("chat_response", chatResponse)
                              intent.putExtra("hr_level", hrLevel)
                              startActivity(intent)
                          } else if (interviewType == 1) {
                              val intent = Intent(this, VoiceInterviewActivity::class.java)
                              intent.putExtra("user_id", userId)
                              intent.putExtra("hr_level_id", hrLevel)
                              intent.putExtra("job_type", jobType)
                              startActivity(intent)
                          } else {
                              Toast.makeText(this, "Tipe wawancara tidak dikenali.", Toast.LENGTH_SHORT).show()
                          }
                        }
                    )
                }
            }
        }
    }
}
