package com.mercu.intrain.ui.chat
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ChatInitResponse
import com.mercu.intrain.API.ChatRequest
import com.mercu.intrain.API.ChatResponse
import com.mercu.intrain.R
import com.mercu.intrain.model.Difficulty
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.theme.intrainPrimary
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DiffSelectScreen(
    navToChat: (sessionId: String, chatResponse: ChatResponse, hrLevel: Int) -> Unit,
    viewModel: DiffSelectViewModel = viewModel()
) {
    val difficulties = listOf(
        Difficulty(1, "Easy", R.drawable.diff_easy, "AI akan mengajukan pertanyaan yang ringan dan langsung ke inti. Cocok untuk pemula yang baru mencoba studi kasus."),
        Difficulty(2, "Medium", R.drawable.diff_med, "AI akan memberikan pertanyaan yang lebih beragam, dengan tingkat kesulitan menengah. Jawabanmu mungkin akan ditanggapi lebih kritis."),
        Difficulty(3, "Hard", R.drawable.diff_hard, "AI akan bersikap kritis dan menantang. Pertanyaannya kompleks, sering ambigu, dan sengaja dibuat untuk menguji logika serta ketegasanmu.")
    )

    val pagerState = rememberPagerState(pageCount = { difficulties.size })

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isLoading by remember { derivedStateOf { viewModel.isLoading } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(intrainPrimary),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Pilih Kesulitan", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp), color = Color.White)

            HorizontalPager(state = pagerState) { page ->
                val item = difficulties[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.title,
                        modifier = Modifier.size(320.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        textAlign = TextAlign.Center,
                        text = item.description,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 30.dp,end = 30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                onClick = {
                    val selected = difficulties[pagerState.currentPage]
                    val userId = SharedPrefHelper(context).getUid().toString()
                    viewModel.initializeChat(
                        userId = userId,
                        hrLevel = selected.id,
                        jobType = "Back-End Engineer",
                        onSuccess = { sessionId, response ->
                            navToChat(sessionId, response, selected.id)
                        },
                        onError = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Select" , color = intrainPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
        }
    }
}

@Composable
@Preview
fun DiffSelectScreenPreview() {
    DiffSelectScreen(navToChat = { _, _, _ -> })
}