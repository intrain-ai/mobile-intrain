package com.mercu.intrain.ui.evaluation

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mercu.intrain.R
import com.mercu.intrain.API.Evaluation

class EvaluationActivity : AppCompatActivity() {

    private lateinit var txtScore: TextView
    private lateinit var txtEvaluatedAt: TextView
    private lateinit var txtRecommendations: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evaluation)

        txtScore = findViewById(R.id.txtScore)
        txtEvaluatedAt = findViewById(R.id.txtEvaluatedAt)
        txtRecommendations = findViewById(R.id.txtRecommendations)

        val evaluation = intent.getParcelableExtra<Evaluation>("evaluation_data")

        if (evaluation != null) {
            txtScore.text = "Score: ${evaluation.score ?: "-"}"
            txtEvaluatedAt.text = "Evaluated at: ${evaluation.evaluatedAt ?: "-"}"

            val listText = evaluation.recommendations?.joinToString("\n• ", prefix = "• ") ?: "No recommendations"
            txtRecommendations.text = listText
        } else {
            Toast.makeText(this, "Data evaluasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
