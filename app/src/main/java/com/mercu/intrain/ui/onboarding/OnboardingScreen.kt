package com.mercu.intrain.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.window.Dialog
import com.mercu.intrain.R
import com.mercu.intrain.theme.intrainPrimary
import com.mercu.intrain.theme.IntrainAccent

@Composable
fun RoadmapInfoCard(
    title: String,
    description: String,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .clickable { onContinueClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Lanjutkan",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Lanjutkan",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalTextApi::class)
@Composable
fun OnboardingDialogModal(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        OnboardingCard(onDismiss)
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalTextApi::class)
@Composable
fun OnboardingCard(onDismiss: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val lastPage = onboardingPages.lastIndex
    val onboardingPage = onboardingPages[page]

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(Modifier.fillMaxSize()) {
            // Tombol Lewati
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Text(
                    "Lewati",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Maskot dengan shadow
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(12.dp, CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.maskot),
                            contentDescription = "Maskot",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Icon fitur
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = onboardingPage.imageRes),
                            contentDescription = onboardingPage.title,
                            modifier = Modifier.size(36.dp)
                        )
                        // Badge Baru jika perlu
                        if (onboardingPage.title == "Jobs" || onboardingPage.title == "Mentorship") {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .background(
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "Baru",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = onboardingPage.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = onboardingPage.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        overflow = TextOverflow.Visible
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress bar modern
                    LinearProgressIndicator(
                        progress = (page + 1) / (lastPage + 1).toFloat(),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Navigasi bawah
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (page > 0) {
                            TextButton(onClick = { page-- }) {
                                Text(
                                    "Sebelumnya",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = { if (page == lastPage) onDismiss() else page++ },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 140.dp)
                                .height(50.dp)
                        ) {
                            Text(
                                if (page == lastPage) "Mulai" else "Lanjut",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}