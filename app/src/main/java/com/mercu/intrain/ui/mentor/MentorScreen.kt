package com.mercu.intrain.ui.mentor

import android.R.attr.onClick
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mercu.intrain.model.Availability
import com.mercu.intrain.model.MentorProfile
import com.mercu.intrain.model.WorkExperience
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.mentor.MentorViewModel
import com.mercu.intrain.ui.theme.InTrainTheme
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MentorshipScreen() {
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefHelper(context) }
    val currentUserId = sharedPref.getUid() ?: ""
    val viewModel: MentorViewModel = viewModel()

    var currentScreen by remember { mutableStateOf("mentor_list") }
    var selectedMentorId by remember { mutableStateOf("") }
    var showAvailabilityDialog by remember { mutableStateOf(false) }
    var showRegistrationForm by remember { mutableStateOf(false) }
    var showSessionDialog by remember { mutableStateOf(false) }
    var selectedAvailability by remember { mutableStateOf<Availability?>(null) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackSessionId by remember { mutableStateOf("") }

    when (currentScreen) {
        "mentor_list" -> {
            MentorListScreen(
                currentUserId = currentUserId,
                onMentorSelected = { id ->
                    selectedMentorId = id
                    currentScreen = "mentor_profile"
                },
                onRegisterClick = { showRegistrationForm = true }
            )
        }
        "mentor_profile" -> {
            MentorProfileScreen(
                mentorId = selectedMentorId,
                currentUserId = currentUserId,
                onBack = { currentScreen = "mentor_list" },
                onSetAvailability = { showAvailabilityDialog = true },
                onAvailabilitySelected = { availability ->
                    selectedAvailability = availability
                    showSessionDialog = true
                }
            )
        }
    }

    if (showAvailabilityDialog) {
        SetAvailabilityDialog(
            mentorId = selectedMentorId,
            onDismiss = { showAvailabilityDialog = false }
        )
    }

    if (showRegistrationForm) {
        RegisterMentorDialog(
            currentUserId = currentUserId,
            onDismiss = { showRegistrationForm = false }
        )
    }

    if (showSessionDialog && selectedAvailability != null) {
        BookSessionDialog(
            availability = selectedAvailability!!,
            currentUserId = currentUserId,
            onDismiss = { showSessionDialog = false },
            onFeedbackRequested = { sessionId ->
                feedbackSessionId = sessionId
                showFeedbackDialog = true
            }
        )
    }

    if (showFeedbackDialog) {
        SubmitFeedbackDialog(
            sessionId = feedbackSessionId,
            onDismiss = {
                showFeedbackDialog = false
                viewModel.resetFeedbackState()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorListScreen(
    currentUserId: String,
    viewModel: MentorViewModel = viewModel(),
    onMentorSelected: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    val state by viewModel.mentorListState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefHelper(context) }
    val isMentor = sharedPref.getIsMentor()

    LaunchedEffect(searchQuery) {
        viewModel.fetchMentors(searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Cari Mentor",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    if (!isMentor) {
                        IconButton(onClick = onRegisterClick) {
                            Icon(
                                Icons.Default.PersonAdd, 
                                contentDescription = "Daftar sebagai Mentor",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Modern Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { 
                    Text(
                        "Cari berdasarkan keahlian...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                placeholder = { 
                    Text(
                        "Contoh: Java, Python, UI/UX, Marketing",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Cari keahlian",
                        tint = MaterialTheme.colorScheme.primary
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                supportingText = {
                    if (searchQuery.isNotEmpty()) {
                        val formattedQuery = searchQuery.lowercase().replace(" ", "_")
                        Text(
                            "Mencari keahlian: $formattedQuery", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            "Ketik keahlian yang ingin dicari", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (state) {
                is MentorViewModel.MentorListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Mencari mentor...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is MentorViewModel.MentorListState.Success -> {
                    val mentors = (state as MentorViewModel.MentorListState.Success).mentors

                    if (mentors.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(), 
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    if (searchQuery.isNotEmpty()) "Tidak ada mentor dengan keahlian tersebut" 
                                    else "Tidak ada mentor tersedia", 
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (searchQuery.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Coba cari dengan kata kunci lain",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Show search results count
                            if (searchQuery.isNotEmpty()) {
                                item {
                                    Text(
                                        "Ditemukan ${mentors.size} mentor dengan keahlian \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                            
                            items(mentors) { mentor ->
                                MentorCard(
                                    mentor = mentor,
                                    currentUserId = currentUserId,
                                    onClick = { onMentorSelected(mentor.id) }
                                )
                            }
                        }
                    }
                }
                is MentorViewModel.MentorListState.Error -> {
                    val error = (state as MentorViewModel.MentorListState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Error, 
                                contentDescription = "Error", 
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Kesalahan: $error", 
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MentorCard(
    mentor: MentorProfile,
    currentUserId: String,
    onClick: () -> Unit
) {
    val isOwnProfile = mentor.userId == currentUserId
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isOwnProfile) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwnProfile) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(20.dp),
        border = if (isOwnProfile) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Modern Avatar with gradient
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        ),
                        CircleShape
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mentor.name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        mentor.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(12.dp))
                    if (isOwnProfile) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                "Anda", 
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Expertise badge with modern design
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { 
                        Text(
                            mentor.expertise,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    mentor.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Lihat profil",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorProfileScreen(
    mentorId: String,
    currentUserId: String,
    viewModel: MentorViewModel = viewModel(),
    onBack: () -> Unit,
    onSetAvailability: () -> Unit,
    onAvailabilitySelected: (Availability) -> Unit
) {
    val profileState by viewModel.profileState.collectAsState()
    val availabilityState by viewModel.availabilityListState.collectAsState()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefHelper(context) }
    val isMentor = sharedPref.getIsMentor()

    val isOwnProfile = remember(profileState, currentUserId) {
        if (profileState is MentorViewModel.ProfileState.Success) {
            (profileState as MentorViewModel.ProfileState.Success).profile.mentorProfile.userId == currentUserId
        } else {
            false
        }
    }

    LaunchedEffect(mentorId) {
        viewModel.fetchMentorProfile(mentorId)
        viewModel.fetchAvailabilities(mentorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profil Mentor",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    if (isMentor && isOwnProfile) {
                        IconButton(onClick = onSetAvailability) {
                            Icon(
                                Icons.Default.Schedule, 
                                contentDescription = "Atur Ketersediaan",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
        ) {
            when (profileState) {
                is MentorViewModel.ProfileState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Memuat profil...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is MentorViewModel.ProfileState.Success -> {
                    val profile = (profileState as MentorViewModel.ProfileState.Success).profile

                    Spacer(modifier = Modifier.height(16.dp))

                    // Modern Profile Header with gradient
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Large Avatar with gradient
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.primaryContainer
                                                )
                                            ),
                                            CircleShape
                                        )
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = profile.mentorProfile.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                Spacer(Modifier.height(20.dp))

                                Text(
                                    profile.mentorProfile.name,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(Modifier.height(8.dp))

                                // Expertise with modern badge
                                FilterChip(
                                    selected = false,
                                    onClick = {},
                                    label = { 
                                        Text(
                                            profile.mentorProfile.expertise,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        labelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )

                                Spacer(Modifier.height(20.dp))

                                Text(
                                    profile.mentorProfile.bio,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp
                                )

                                if (isOwnProfile) {
                                    Spacer(Modifier.height(20.dp))
                                    FilterChip(
                                        selected = true,
                                        onClick = {},
                                        label = { 
                                            Text(
                                                "Profil Anda",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Verified, 
                                                contentDescription = "Terverifikasi",
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Availability Section
                    Text(
                        "Ketersediaan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))

                    when (availabilityState) {
                        is MentorViewModel.AvailabilityListState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        is MentorViewModel.AvailabilityListState.Success -> {
                            val availabilities = (availabilityState as MentorViewModel.AvailabilityListState.Success).availabilities

                            if (availabilities.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Tidak ada slot tersedia",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                availabilities.forEach { availability ->
                                    AvailabilitySlot(
                                        availability = availability,
                                        onBookClick = { onAvailabilitySelected(availability) },
                                        enabled = !isOwnProfile
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                        is MentorViewModel.AvailabilityListState.Error -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Gagal memuat ketersediaan",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Work Experience Section
                    Text(
                        "Pengalaman Kerja",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))

                    if (profile.workExperiences.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Tidak ada pengalaman kerja",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        profile.workExperiences.forEach { exp ->
                            ExperienceItem(exp)
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                is MentorViewModel.ProfileState.Error -> {
                    val error = (profileState as MentorViewModel.ProfileState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Error, 
                                contentDescription = "Error", 
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Kesalahan: $error", 
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExperienceItem(exp: WorkExperience) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Modern icon with gradient background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            ),
                            CircleShape
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = "Pekerjaan",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        exp.jobTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        exp.companyName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Modern timeline indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )
                Spacer(Modifier.width(12.dp))
                val period = buildString {
                    append("${monthName(exp.startMonth)} ${exp.startYear} - ")
                    append(if (exp.isCurrent) "Sekarang" else "${monthName(exp.endMonth)} ${exp.endYear}")
                }
                Text(
                    period,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (exp.isCurrent) {
                    Spacer(Modifier.width(8.dp))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Text(
                            "Sekarang",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                exp.jobDesc,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
        }
    }
}

private fun monthName(month: Int?): String = when (month) {
    1 -> "Januari"
    2 -> "Februari"
    3 -> "Maret"
    4 -> "April"
    5 -> "Mei"
    6 -> "Juni"
    7 -> "Juli"
    8 -> "Agustus"
    9 -> "September"
    10 -> "Oktober"
    11 -> "November"
    12 -> "Desember"
    else -> "N/A"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AvailabilitySlot(
    availability: Availability,
    onBookClick: () -> Unit,
    enabled: Boolean = true
) {
    val isoFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE_TIME }
    val displayFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d - h:mm a") }

    val startDateTime = remember(availability.startDatetime) {
        java.time.LocalDateTime.parse(availability.startDatetime, isoFormatter).format(displayFormatter)
    }
    val endDateTime = remember(availability.endDatetime) {
        java.time.LocalDateTime.parse(availability.endDatetime, isoFormatter).format(displayFormatter)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = BorderStroke(
            1.dp, 
            if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Modern time icon with background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "Waktu",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Sesi Konsultasi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$startDateTime - $endDateTime",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onBookClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (enabled) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (enabled) 4.dp else 0.dp
                )
            ) {
                Text(
                    if (enabled) "Pesan Sesi" else "Tidak Tersedia",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetAvailabilityDialog(
    mentorId: String,
    viewModel: MentorViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedStartTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var selectedEndTime by remember { mutableStateOf(LocalTime.of(10, 0)) }

    val state by viewModel.availabilityState.collectAsState()

    val formattedDate = remember(selectedDate) {
        selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID")))
    }

    val formattedStartTime = remember(selectedStartTime) {
        selectedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    val formattedEndTime = remember(selectedEndTime) {
        selectedEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    val startDateTime = remember(selectedDate, selectedStartTime) {
        LocalDateTime.of(selectedDate, selectedStartTime)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    val endDateTime = remember(selectedDate, selectedEndTime) {
        LocalDateTime.of(selectedDate, selectedEndTime)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Ketersediaan", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                // Date Picker Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Tanggal")
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tanggal", style = MaterialTheme.typography.bodyMedium)
                            Text(formattedDate, style = MaterialTheme.typography.titleMedium)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Pilih tanggal")
                    }
                }

                // Start Time Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStartTimePicker = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = "Waktu Mulai")
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Waktu Mulai", style = MaterialTheme.typography.bodyMedium)
                            Text(formattedStartTime, style = MaterialTheme.typography.titleMedium)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Pilih waktu mulai")
                    }
                }

                // End Time Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndTimePicker = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = "Waktu Selesai")
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Waktu Selesai", style = MaterialTheme.typography.bodyMedium)
                            Text(formattedEndTime, style = MaterialTheme.typography.titleMedium)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = "Pilih waktu selesai")
                    }
                }

                if (selectedEndTime.isBefore(selectedStartTime)) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "\u26a0 Waktu selesai harus setelah waktu mulai",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                when (state) {
                    is MentorViewModel.AvailabilityState.Success -> {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "\u2713 Ketersediaan berhasil diatur!",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    is MentorViewModel.AvailabilityState.Error -> {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "\u26a0 ${(state as MentorViewModel.AvailabilityState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            if (state is MentorViewModel.AvailabilityState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.setAvailability(mentorId, startDateTime, endDateTime)
                    },
                    enabled = selectedEndTime.isAfter(selectedStartTime),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Simpan Jadwal", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Batal", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )

    // Inline Date Picker
    if (showDatePicker) {
        Dialog(onDismissRequest = { showDatePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(320.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { selectedDate = selectedDate.minusMonths(1) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Bulan sebelumnya")
                        }

                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = { selectedDate = selectedDate.plusMonths(1) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Bulan berikutnya")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.size(36.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    val yearMonth = YearMonth.from(selectedDate)
                    val daysInMonth = yearMonth.lengthOfMonth()
                    val firstDayOfMonth = selectedDate.withDayOfMonth(1)
                    val startOffset = firstDayOfMonth.dayOfWeek.value % 7

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(280.dp)
                    ) {
                        items(42) { index ->
                            val dayIndex = index - startOffset + 1
                            val isCurrentMonth = dayIndex in 1..daysInMonth
                            val date = if (isCurrentMonth) {
                                firstDayOfMonth.plusDays((dayIndex - 1).toLong())
                            } else null

                            val isToday = date == LocalDate.now()
                            val isSelected = date == selectedDate

                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                                    .background(
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = isCurrentMonth) {
                                        if (isCurrentMonth) {
                                            selectedDate = date!!
                                            showDatePicker = false
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCurrentMonth) {
                                    Text(
                                        text = dayIndex.toString(),
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.onPrimary
                                            isToday -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Dipilih: ${selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("id", "ID")))}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { showDatePicker = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Pilih Tanggal")
                    }
                }
            }
        }
    }

    // Time Pickers (You should already have TimePickerDialog Composables)
    if (showStartTimePicker) {
        TimePickerDialog(
            title = "Pilih Waktu Mulai",
            initialTime = selectedStartTime,
            onDismiss = { showStartTimePicker = false },
            onTimeSelected = { time ->
                selectedStartTime = time
                showStartTimePicker = false
                if (!selectedEndTime.isAfter(time)) {
                    selectedEndTime = time.plusHours(1)
                }
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            title = "Pilih Waktu Selesai",
            initialTime = selectedEndTime,
            onDismiss = { showEndTimePicker = false },
            onTimeSelected = { time ->
                if (time.isAfter(selectedStartTime)) {
                    selectedEndTime = time
                    showEndTimePicker = false
                }
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(
    title: String,
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(24.dp))

                // Time display
                Text(
                    selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(Modifier.height(24.dp))

                // Time selector
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Hour selector
                    TimeSelector(
                        values = (0..23).toList(),
                        selectedValue = selectedTime.hour,
                        onValueSelected = { hour ->
                            selectedTime = LocalTime.of(hour, selectedTime.minute)
                        },
                        label = "Jam"
                    )

                    Text(":", style = MaterialTheme.typography.displayMedium)

                    // Minute selector
                    TimeSelector(
                        values = listOf(0, 15, 30, 45),
                        selectedValue = selectedTime.minute,
                        onValueSelected = { minute ->
                            selectedTime = LocalTime.of(selectedTime.hour, minute)
                        },
                        label = "Menit"
                    )
                }

                Spacer(Modifier.height(32.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = { onTimeSelected(selectedTime) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Pilih")
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSelector(
    values: List<Int>,
    selectedValue: Int,
    onValueSelected: (Int) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall)

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .height(200.dp)
                .verticalScroll(rememberScrollState())
        ) {
            values.forEach { value ->
                val formattedValue = when (label) {
                    "Jam" -> String.format("%02d", value)
                    else -> String.format("%02d", value)
                }

                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(48.dp)
                        .background(
                            color = if (value == selectedValue) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onValueSelected(value) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formattedValue,
                        color = if (value == selectedValue) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (value == selectedValue) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMentorDialog(
    currentUserId: String,
    viewModel: MentorViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    var expertise by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    val state by viewModel.registerState.collectAsState()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefHelper(context) }

    LaunchedEffect(state) {
        if (state is MentorViewModel.RegisterState.Success) {
            sharedPref.saveIsMentor(true)
            delay(2000)
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Daftar sebagai Mentor",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = expertise,
                    onValueChange = { expertise = it },
                    label = { 
                        Text(
                            "Keahlian Anda",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    placeholder = {
                        Text(
                            "Contoh: Java, Python, UI/UX Design",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { 
                        Text(
                            "Bio Anda",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    placeholder = {
                        Text(
                            "Ceritakan pengalaman dan keahlian Anda...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    ),
                    maxLines = 4,
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                when (state) {
                    is MentorViewModel.RegisterState.Success -> {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Berhasil",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Pendaftaran berhasil! Anda sekarang adalah mentor.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    is MentorViewModel.RegisterState.Error -> {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    (state as MentorViewModel.RegisterState.Error).message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            if (state is MentorViewModel.RegisterState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Button(
                    onClick = {
                        if (currentUserId.isNotEmpty()) {
                            viewModel.registerMentor(currentUserId, expertise, bio)
                        }
                    },
                    enabled = expertise.isNotBlank() && bio.isNotBlank() && currentUserId.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        "Daftar sebagai Mentor",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Batal",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSessionDialog(
    availability: Availability,
    currentUserId: String,
    viewModel: MentorViewModel = viewModel(),
    onDismiss: () -> Unit,
    onFeedbackRequested: (String) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.sessionState.collectAsState()

    val isoFormatter = remember { DateTimeFormatter.ISO_LOCAL_DATE_TIME }
    val displayFormatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d - h:mm a") }

    val start = remember(availability.startDatetime) {
        java.time.LocalDateTime.parse(availability.startDatetime, isoFormatter).format(displayFormatter)
    }
    val end = remember(availability.endDatetime) {
        java.time.LocalDateTime.parse(availability.endDatetime, isoFormatter).format(displayFormatter)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pesan Sesi") },
        text = {
            Column {
                Text("Mulai: $start", style = MaterialTheme.typography.bodyMedium)
                Text("Selesai: $end", style = MaterialTheme.typography.bodyMedium)

                if (currentUserId.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Anda perlu masuk untuk memesan sesi",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                when (state) {
                    is MentorViewModel.SessionState.Success -> {
                        val session = (state as MentorViewModel.SessionState.Success).session

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Sesi berhasil dipesan!",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tautan Meet:", style = MaterialTheme.typography.bodyMedium)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            SelectionContainer {
                                Text(
                                    session.meetLink,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Meet Link", session.meetLink)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Tautan disalin", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, "Salin tautan")
                            }
                        }

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(session.meetLink)
                                        setPackage("com.google.android.apps.meetings")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(session.meetLink))
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, "Buka di Google Meet", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Buka di Google Meet")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dijadwalkan pada: ${session.scheduledAt}")

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                onFeedbackRequested(session.id)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.RateReview, "Beri Umpan Balik")
                            Spacer(Modifier.width(8.dp))
                            Text("Beri Umpan Balik")
                        }
                    }
                    is MentorViewModel.SessionState.Error -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            (state as MentorViewModel.SessionState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentUserId.isNotEmpty()) {
                        viewModel.bookSession(currentUserId, availability.id)
                    }
                },
                enabled = currentUserId.isNotEmpty() &&
                        (state !is MentorViewModel.SessionState.Success),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Konfirmasi Pemesanan")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Batal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitFeedbackDialog(
    sessionId: String,
    viewModel: MentorViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }
    val state by viewModel.feedbackState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is MentorViewModel.FeedbackState.Success) {
            delay(1500)
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Umpan Balik Sesi", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text("Bagaimana penilaian Anda tentang sesi ini?", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { rating = i }
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Rating $i",
                                modifier = Modifier.fillMaxSize(),
                                tint = if (i <= rating) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Umpan balik Anda") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )

                when (state) {
                    is MentorViewModel.FeedbackState.Success -> {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "✓ Umpan balik berhasil dikirim!",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    is MentorViewModel.FeedbackState.Error -> {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "⚠ ${(state as MentorViewModel.FeedbackState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            if (state is MentorViewModel.FeedbackState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (rating > 0) {
                            viewModel.submitFeedback(sessionId, rating, feedbackText)
                        }
                    },
                    enabled = rating > 0,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kirim Umpan Balik", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal")
            }
        }
    )
}