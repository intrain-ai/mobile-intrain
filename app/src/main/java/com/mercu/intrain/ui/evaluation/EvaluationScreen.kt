package com.mercu.intrain.ui.evaluation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercu.intrain.API.Evaluation
import com.mercu.intrain.theme.intrainPrimary

@Composable
fun EvaluationScreen(evaluation: Evaluation) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(intrainPrimary)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Evaluation Result",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Score:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                ScoreBar(score = evaluation.score)

                Text(
                    text = "Evaluated at: ${evaluation.evaluatedAt ?: "-"}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Recommendations",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = evaluation.recommendations?.joinToString("\n• ", prefix = "• ")
                        ?: "No recommendations",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
@Preview
fun EvaluationScreenPreview(){
    EvaluationScreen(evaluation = Evaluation())
}


@Composable
fun ScoreBar(score: Int?) {
    val maxScore = 10
    val validScore = score?.coerceIn(0, maxScore) ?: 0

    val color = when {
        score == null -> Color.Gray
        validScore <= 3 -> Color.Red
        validScore <= 7 -> Color(0xFFFFA000) // Kuning
        else -> Color(0xFF4CAF50)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(validScore / 10f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$validScore / $maxScore",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


