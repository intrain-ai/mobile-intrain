package com.mercu.intrain.ui.cvcheck

import ReviewViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mercu.intrain.API.CvResponse
import com.mercu.intrain.API.SectionsItem
import com.mercu.intrain.R
import com.mercu.intrain.theme.intrainPrimary
import com.spr.jetpack_loading.components.indicators.BallSpinFadeLoaderIndicator
import com.spr.jetpack_loading.components.indicators.PacmanIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReviewScreen(viewModel: ReviewViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pdfPreview by viewModel.pdfPreview.collectAsState()
    val result by viewModel.cvResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showInitialUI by remember { mutableStateOf(true) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.setSelectedPdfUri(context, uri)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(intrainPrimary),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showInitialUI && !isLoading && result == null,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.senag),
                    contentDescription = null,
                    modifier = Modifier
                        .size(320.dp)
                        .padding(bottom = 2.dp)
                )
                Text(
                    text = "Analisa CV",
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp, start = 30.dp, end = 30.dp, top = 10.dp)
                )
                Text(
                    text = "Unggah CV kamu di sini untuk mulai analisa otomatis dari AI kami",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 30.dp, end = 30.dp, top = 10.dp)
                )
                Button(
                    onClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Text("Upload CV PDF", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            showInitialUI = false
                            viewModel.uploadPdf(context)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Text("Analisa dengan AI", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.diff_easy),
                    contentDescription = null,
                    modifier = Modifier.size(320.dp).padding(bottom = 10.dp)
                )
                Spacer(modifier = Modifier.height(44.dp))
                BallSpinFadeLoaderIndicator()
            }
        }

        AnimatedVisibility(
            visible = result != null && !isLoading,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            ResultSection(result!!)
        }
    }
}

@Composable
fun ResultSection(response: CvResponse) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column (
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("Overall Feedback", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column (
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(response.review?.overallFeedback ?: "")
                     }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            val atsPassed = response.review?.atsPassed == true
            val backgroundColor = if (atsPassed) Color(0xFFDFF5DD) else Color(0xFFFFE3E3)
            val iconColor = if (atsPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
            val icon = if (atsPassed) Icons.Default.Check else Icons.Default.Warning
            val statusText = if (atsPassed) "CV Berbentuk ATS" else "CV Tidak Berbentuk ATS"

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(statusText, fontWeight = FontWeight.Bold, color = iconColor)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    "Section Reviews",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(response.sections ?: emptyList()) { section ->
            section?.let {
                if (section.section?.contains("profile", ignoreCase = true) == true ||
                    section.section?.contains("education", ignoreCase = true) == true ||
                    section.section?.contains("experience", ignoreCase = true) == true ||
                    section.section?.contains("skill", ignoreCase = true) == true ||
                    section.section?.contains("certification", ignoreCase = true) == true ||
                    section.section?.contains("portfolio", ignoreCase = true) == true) {
                    ExpandableSection(section = it)
                } else {
                    SectionReviewItem(section = it)
                }
            }
        }
    }
}

@Composable
fun ExpandableSection(section: SectionsItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (section.needsImprovement == true) Icons.Default.Warning else Icons.Default.Check,
                    contentDescription = null,
                    tint = if (section.needsImprovement == true)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    section.section?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "-",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = section.feedback ?: "",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SectionReviewItem(section: SectionsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (section.needsImprovement == true) Icons.Default.Warning else Icons.Default.Check,
                contentDescription = null,
                tint = if (section.needsImprovement == true) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    section.section?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "-",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(section.feedback ?: "")
            }
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    ReviewScreen()
}

@Preview(showBackground = true)
@Composable
fun AnimatedVisibilityPreview() {
    var visible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { visible = !visible }) {
            Text("Toggle")
        }

        AnimatedVisibility(visible = visible) {
            Text("This is animated content", modifier = Modifier.padding(top = 16.dp))
        }
    }
}
