package com.mercu.intrain.ui.cvcheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mercu.intrain.ui.cvcheck.ReviewScreen

class ReviewComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme { // Anda bisa pakai ini meskipun belum punya tema kustom
                Surface {
                    ReviewScreen()
                }
            }
        }
    }
}
