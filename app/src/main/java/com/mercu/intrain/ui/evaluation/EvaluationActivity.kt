package com.mercu.intrain.ui.evaluation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.mercu.intrain.API.Evaluation
import com.mercu.intrain.ui.theme.InTrainTheme

class EvaluationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val evaluation = intent.getParcelableExtra<Evaluation>("evaluation_data")

        setContent {
            InTrainTheme {
                if (evaluation != null) {
                    EvaluationScreen(evaluation = evaluation)
                } else {
                    Text("Evaluation data not found.")
                }
            }
        }
    }
}

