package com.mercu.intrain.ui.voice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mercu.intrain.API.Evaluation
import com.mercu.intrain.API.VoiceInterviewInitResponse
import com.mercu.intrain.API.VoiceInterviewContinueResponse
import java.io.File
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.mercu.intrain.ui.theme.InTrainTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.Navigation.findNavController

class VoiceInterviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("user_id")
        val hrLevelId = intent.getIntExtra("hr_level_id", -1)
        val jobType = intent.getStringExtra("job_type")
        setContent {
            InTrainTheme {
                VoiceInterviewScreen(
                    initialUserId = userId,
                    initialHrLevelId = hrLevelId,
                    initialJobType = jobType
                )
            }
        }
    }
}

@Composable
fun VoiceInterviewScreen(
    initialUserId: String? = null,
    initialHrLevelId: Int? = null,
    initialJobType: String? = null
) {
    val context = LocalContext.current
    val viewModel: VoiceInterviewViewModel = viewModel(factory = viewModelFactory {
        initializer { VoiceInterviewViewModel(context.applicationContext as android.app.Application) }
    })
    val state by viewModel.state.collectAsState()
    var userId by remember { mutableStateOf(initialUserId ?: "") }
    var hrLevelId by remember { mutableStateOf(initialHrLevelId ?: 1) }
    var jobType by remember { mutableStateOf(initialJobType ?: "") }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    var permissionDenied by remember { mutableStateOf(false) }
    var audioError by remember { mutableStateOf<String?>(null) }

    // Untuk reset audioFile setelah pertanyaan baru
    var lastSessionId by remember { mutableStateOf("") }
    // Untuk retry jika error
    var lastSessionIdForRetry by remember { mutableStateOf<String?>(null) }
    var lastResponseTextForRetry by remember { mutableStateOf<String?>(null) }
    var lastResponseAudioForRetry by remember { mutableStateOf<String?>(null) }
    var lastTranscriptForRetry by remember { mutableStateOf<String?>(null) }
    var lastPlayedAudioUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(initialUserId, initialHrLevelId, initialJobType) {
        if (!initialUserId.isNullOrBlank() && initialHrLevelId != null && initialHrLevelId > 0 && !initialJobType.isNullOrBlank()) {
            viewModel.initInterview(initialUserId, initialHrLevelId, initialJobType)
        }
    }

    val startRecording = {
        val file = File(context.cacheDir, "recorded_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.mp3")
        val rec = MediaRecorder()
        rec.setAudioSource(MediaRecorder.AudioSource.MIC)
        rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        rec.setAudioEncodingBitRate(128000)
        rec.setAudioSamplingRate(44100)
        rec.setOutputFile(file.absolutePath)
        try {
            rec.prepare()
            rec.start()
            recorder = rec
            recordedFile = file
            isRecording = true
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to start recording: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
    val stopRecording = {
        try {
            recorder?.stop()
            recorder?.release()
            isRecording = false
            audioFile = recordedFile
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to stop recording: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            permissionDenied = false
            startRecording()
        } else {
            permissionDenied = true
        }
    }

    val pickAudioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            if (file != null) audioFile = file
            else Toast.makeText(context, "Failed to get audio file", Toast.LENGTH_SHORT).show()
        }
    }

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    fun playAudioWithExoPlayer(uri: String) {
        if (uri != lastPlayedAudioUrl) {
            try {
                exoPlayer.setMediaItem(MediaItem.fromUri(uri))
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                lastPlayedAudioUrl = uri
            } catch (e: Exception) {
                Log.e("VoiceInterview", "Failed to play audio: ${e.localizedMessage}")
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Voice Interview",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Simulasi interview AI dengan suara. Jawab pertanyaan dengan rekaman suara atau upload file mp3.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            if (state is VoiceInterviewState.Loading) {
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator()
            }
            if (state is VoiceInterviewState.Error) {
                Text((state as VoiceInterviewState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            if (permissionDenied) {
                Text("Microphone permission denied. Please enable it in settings to record.", color = MaterialTheme.colorScheme.error)
            }
            if (state is VoiceInterviewState.Idle) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        TextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        TextField(value = jobType, onValueChange = { jobType = it }, label = { Text("Job Type") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        TextField(value = hrLevelId.toString(), onValueChange = { v -> hrLevelId = v.toIntOrNull() ?: 1 }, label = { Text("HR Level ID") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.initInterview(userId, hrLevelId, jobType) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Start Voice Interview", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
            if (state is VoiceInterviewState.InitSuccess) {
                val resp = (state as VoiceInterviewState.InitSuccess).response
                // Autoplay audio jika ada response_audio dan belum pernah diputar
                LaunchedEffect(resp.responseAudio) {
                    if (!resp.responseAudio.isNullOrBlank() && resp.responseAudio != lastPlayedAudioUrl) {
                        playAudioWithExoPlayer(resp.responseAudio)
                        lastPlayedAudioUrl = resp.responseAudio
                        audioError = null
                    }
                }
                // Reset audioFile jika sessionId berubah (pertanyaan baru)
                LaunchedEffect(resp.sessionId) {
                    if (resp.sessionId != lastSessionId) {
                        audioFile = null
                        recordedFile = null
                        lastSessionId = resp.sessionId ?: ""
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pertanyaan AI:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(resp.responseText ?: "-", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
                        if (!resp.responseAudio.isNullOrEmpty()) {
                            Button(
                                onClick = { playAudioWithExoPlayer(resp.responseAudio!!) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Play AI Audio", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Jawaban Anda:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isRecording) {
                                Button(
                                    onClick = {
                                        permissionDenied = false
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            startRecording()
                                        } else {
                                            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Record", color = MaterialTheme.colorScheme.onPrimary) }
                            } else {
                                Button(
                                    onClick = { stopRecording() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) { Text("Stop", color = MaterialTheme.colorScheme.onError) }
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { pickAudioLauncher.launch("audio/mpeg") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) { Text("Upload mp3", color = MaterialTheme.colorScheme.onPrimary) }
                        }
                        if (audioFile != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Selected: ${audioFile!!.name}", style = MaterialTheme.typography.bodySmall)
                            Row {
                                Button(
                                    onClick = {
                                        audioFile?.let { playAudioWithExoPlayer(it.absolutePath) }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Play Recording", color = MaterialTheme.colorScheme.onPrimary) }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.continueInterview(resp.sessionId ?: "", audioFile!!)
                                        audioFile = null
                                        recordedFile = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Send Answer", color = MaterialTheme.colorScheme.onPrimary) }
                            }
                        }
                    }
                }
            }
            if (state is VoiceInterviewState.ContinueSuccess) {
                val resp = (state as VoiceInterviewState.ContinueSuccess).response
                // Autoplay audio jika ada response_audio dan belum pernah diputar
                LaunchedEffect(resp.responseAudio) {
                    if (!resp.responseAudio.isNullOrBlank() && resp.responseAudio != lastPlayedAudioUrl) {
                        playAudioWithExoPlayer(resp.responseAudio)
                        lastPlayedAudioUrl = resp.responseAudio
                        audioError = null
                    }
                }
                // Reset audioFile jika sessionId berubah (pertanyaan baru)
                LaunchedEffect(resp.sessionId) {
                    if (resp.sessionId != lastSessionId) {
                        audioFile = null
                        recordedFile = null
                        lastSessionId = resp.sessionId ?: ""
                    }
                }
                if (resp.evaluation == null) {
                    // Simpan data untuk retry jika error
                    lastSessionIdForRetry = resp.sessionId
                    lastResponseTextForRetry = resp.responseText
                    lastResponseAudioForRetry = resp.responseAudio
                    lastTranscriptForRetry = resp.transcript
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pertanyaan AI:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(resp.responseText ?: "-", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
                        if (!resp.responseAudio.isNullOrEmpty()) {
                            Button(
                                onClick = { playAudioWithExoPlayer(resp.responseAudio) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Play AI Audio", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Transcript:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(resp.transcript ?: "-", style = MaterialTheme.typography.bodyMedium)
                        if (resp.evaluation != null) {
                            Spacer(Modifier.height(8.dp))
                            EvaluationSection(resp.evaluation)
                        }
                    }
                }
                if (resp.evaluation == null) {
                    // Interview belum selesai, tampilkan input rekaman/upload untuk lanjut
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Jawaban Anda:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isRecording) {
                                    Button(
                                        onClick = {
                                            permissionDenied = false
                                            if (androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                                startRecording()
                                            } else {
                                                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) { Text("Record", color = MaterialTheme.colorScheme.onPrimary) }
                                } else {
                                    Button(
                                        onClick = { stopRecording() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) { Text("Stop", color = MaterialTheme.colorScheme.onError) }
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = { pickAudioLauncher.launch("audio/mpeg") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Upload mp3", color = MaterialTheme.colorScheme.onPrimary) }
                            }
                            if (audioFile != null) {
                                Spacer(Modifier.height(8.dp))
                                Text("Selected: ${audioFile!!.name}", style = MaterialTheme.typography.bodySmall)
                                Row {
                                    Button(
                                        onClick = {
                                            audioFile?.let { playAudioWithExoPlayer(it.absolutePath) }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) { Text("Play Recording", color = MaterialTheme.colorScheme.onPrimary) }
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.continueInterview(resp.sessionId ?: "", audioFile!!)
                                            audioFile = null
                                            recordedFile = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) { Text("Send Answer", color = MaterialTheme.colorScheme.onPrimary) }
                                }
                            }
                        }
                    }
                } else {
                    // Interview selesai, tampilkan tombol restart
                    Button(
                        onClick = { (context as? Activity)?.finish() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Back to Home", color = MaterialTheme.colorScheme.onPrimary) }
                }
            }
            if (state is VoiceInterviewState.Error && lastSessionIdForRetry != null) {
                Text((state as VoiceInterviewState.Error).message, color = MaterialTheme.colorScheme.error)
                // Render ulang UI pertanyaan AI, transcript, dan input jawaban
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("AI Response:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(lastResponseTextForRetry ?: "-", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 8.dp))
                        if (!lastResponseAudioForRetry.isNullOrEmpty()) {
                            Button(
                                onClick = { playAudioWithExoPlayer(lastResponseAudioForRetry!!) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Play AI Audio", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Transcript:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(lastTranscriptForRetry ?: "-", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Jawaban Anda:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isRecording) {
                                Button(
                                    onClick = {
                                        permissionDenied = false
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            startRecording()
                                        } else {
                                            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Record", color = MaterialTheme.colorScheme.onPrimary) }
                            } else {
                                Button(
                                    onClick = { stopRecording() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) { Text("Stop", color = MaterialTheme.colorScheme.onError) }
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { pickAudioLauncher.launch("audio/mpeg") },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) { Text("Upload mp3", color = MaterialTheme.colorScheme.onPrimary) }
                        }
                        if (audioFile != null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Selected: ${audioFile!!.name}", style = MaterialTheme.typography.bodySmall)
                            Row {
                                Button(
                                    onClick = {
                                        audioFile?.let { playAudioWithExoPlayer(it.absolutePath) }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Play Recording", color = MaterialTheme.colorScheme.onPrimary) }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.continueInterview(lastSessionIdForRetry ?: "", audioFile!!)
                                        audioFile = null
                                        recordedFile = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Send Answer", color = MaterialTheme.colorScheme.onPrimary) }
                            }
                        }
                    }
                }
            }
            if (audioError != null) {
                Text(audioError!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun InterviewResponseSection(text: String?, audioUrl: String?, exoPlayer: ExoPlayer?, onPlay: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("AI Question/Response:", style = MaterialTheme.typography.titleMedium)
        Text(text ?: "-", style = MaterialTheme.typography.bodyLarge)
        if (!audioUrl.isNullOrEmpty()) {
            Button(onClick = { onPlay(audioUrl) }) { Text("Play AI Audio") }
        }
    }
}

@Composable
fun EvaluationSection(evaluation: Evaluation) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text("Evaluation Score: ${evaluation.score}", style = MaterialTheme.typography.titleMedium)
        Text("Recommendations:", style = MaterialTheme.typography.titleSmall)
        evaluation.recommendations?.forEach {
            Text("- $it", style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun uriToFile(context: android.content.Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "temp_audio.mp3")
    file.outputStream().use { inputStream.copyTo(it) }
    return file
} 