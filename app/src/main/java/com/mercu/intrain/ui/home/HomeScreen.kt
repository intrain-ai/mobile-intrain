package com.mercu.intrain.ui.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mercu.intrain.API.Article
import com.mercu.intrain.R
import com.mercu.intrain.sharedpref.SharedPrefHelper
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mercu.intrain.ui.roadmap.RoadmapScreen


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onCourseClick: () -> Unit,
    onCVCheckClick: () -> Unit,
    onChatBotClick: () -> Unit,
    onNavigateToRoadmapDetail: (String) -> Unit
) {
    val name by viewModel.name.observeAsState("Hello, User!")
    val newsList by viewModel.newsArticles.observeAsState(emptyList())
    val roadmapDetail by viewModel.selectedRoadmap.collectAsState()
    val percentage by viewModel.progressPercentage.observeAsState(0)

    val lifecycleOwner = LocalLifecycleOwner.current
    var shouldRefresh by remember { mutableStateOf(false) }

    // Observe lifecycle events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                shouldRefresh = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.getLatestRoadmapId { roadmapId ->
                if (!roadmapId.isNullOrEmpty()) {
                    viewModel.getRoadmapDetail(roadmapId)
                    viewModel.getRoadmapProgress()
                }
            }
            shouldRefresh = false
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = name, style = MaterialTheme.typography.titleLarge, fontSize = 32.sp)

            Spacer(modifier = Modifier.height(24.dp))


            val progress = percentage

            val roadmapName = roadmapDetail?.title ?: "No Roadmap Selected"
            val courseDescription = roadmapDetail?.description ?: "No Description Available"


            RoadmapProgressSection(progress, roadmapName, courseDescription,  onContinueClick = {
                val roadmapId = viewModel.selectedRoadmap.value?.id ?: return@RoadmapProgressSection
                onNavigateToRoadmapDetail(roadmapId)
            })
            Spacer(modifier = Modifier.height(24.dp))

            Text("Menu", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(12.dp))

            FeatureButtons(
                onCourseClick = onCourseClick,
                onCVCheckClick = onCVCheckClick,
                onChatBotClick = onChatBotClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Berita Terkini", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            NewsCarousel(newsList)


        }
    }
}

@Composable
fun RoadmapProgressSection(
    progress: Int,
    title: String,
    description: String,
    onContinueClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProgressCard(
            progress = progress,
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()

        )

        RoadmapInfoCard(
            title = title,
            description = description,
            onContinueClick = onContinueClick,
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
        )
    }
}


@Composable
fun ProgressCard(progress: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF444CB2))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Progress", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            // Layered progress: background and actual progress
            Box(contentAlignment = Alignment.Center) {
                // Background indicator (faded or semi-transparent)
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(50.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    strokeWidth = 5.dp
                )
                // Foreground indicator (actual progress)
                CircularProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.size(50.dp),
                    color = Color.White,
                    strokeWidth = 5.dp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("$progress%", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}


@Composable
fun RoadmapInfoCard(
    title: String,
    description: String,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5A63DA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text(description, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onContinueClick() }
            ) {
                Text("Lanjutkan", color = Color.White, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}




@Composable
fun NewsCarousel(news: List<Article>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(news) { item ->
            NewsCard(item)
        }
    }
}

@Composable
fun NewsCard(article: Article) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = article.title ?: "No title",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
        }
    }
}


@Composable
fun FeatureButtons(
    onCourseClick: () -> Unit,
    onCVCheckClick: () -> Unit,
    onChatBotClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FeatureIconWithLabel("CV Check", R.drawable.ic_cv_checker, onCVCheckClick, Modifier.weight(1f))
        FeatureIconWithLabel("InTrain Bot", R.drawable.ic_chat, onChatBotClick, Modifier.weight(1f))
        FeatureIconWithLabel("Courses", R.drawable.ic_course, onCourseClick, Modifier.weight(1f))
    }
}

@Composable
fun FeatureIconWithLabel(
    label: String,
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(width = 125.dp, height = 125.dp)
                .clip(RoundedCornerShape(12.dp)) // rectangle with rounded corners
                .background(Color(0xFF5A63DA))  // optional background
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp) // optional padding inside the icon box
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}




