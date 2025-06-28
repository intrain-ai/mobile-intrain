package com.mercu.intrain.ui.chat

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercu.intrain.API.ChatResponse
import com.mercu.intrain.API.Evaluation
import com.mercu.intrain.R
import com.mercu.intrain.model.Message
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatResponse: ChatResponse,
    sessionId: String,
    onNavigateToEvaluation: (Evaluation) -> Unit,
    viewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var input by remember { mutableStateOf(TextFieldValue("")) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.initializeFirstMessage(chatResponse)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            messages.forEach { msg ->
                ChatBubble(
                    message = msg.text,
                    isUser = msg.isUser,
                    name = if (msg.isUser) "You" else "AI Assistant",
                    profilePic = if (msg.isUser) R.drawable.ic_person else R.drawable.mikir_1
                )
            }

            if (isLoading) {
                ChatBubble(
                    message = "Sedang mengetik...",
                    isUser = false,
                    name = "AI Assistant",
                    profilePic = R.drawable.mikir_1
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .heightIn(min = 48.dp),
                enabled = !isLoading
            )

            IconButton(
                onClick = {
                    val text = input.text.trim()
                    if (text.isNotEmpty()) {
                        viewModel.sendMessage(sessionId, text, onNavigateToEvaluation)
                        input = TextFieldValue()
                    }
                },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    name: String,
    profilePic: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Icon(
                painter = painterResource(id = R.drawable.mikir_1),
                contentDescription = "Bot",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
                tint = Color.Unspecified
            )
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Text(name, style = MaterialTheme.typography.labelMedium)
            Surface(
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = message,
                    modifier = Modifier
                        .padding(12.dp)
                        .widthIn(max = 280.dp),
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (isUser) {
            Icon(
                painter = painterResource(id = profilePic),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}



