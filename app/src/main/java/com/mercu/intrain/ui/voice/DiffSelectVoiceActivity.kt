package com.mercu.intrain.ui.voice

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class DiffSelectVoiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    DiffSelectVoiceScreen(
                        onSelect = { userId, hrLevel, jobType ->
                            val intent = Intent(this, VoiceInterviewActivity::class.java).apply {
                                putExtra("user_id", userId)
                                putExtra("hr_level_id", hrLevel)
                                putExtra("job_type", jobType)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
} 