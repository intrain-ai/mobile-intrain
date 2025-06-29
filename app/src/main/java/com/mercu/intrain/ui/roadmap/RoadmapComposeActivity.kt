package com.mercu.intrain.ui.roadmap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mercu.intrain.ui.course.DetailCourseActivity
import com.mercu.intrain.ui.theme.InTrainTheme

@OptIn(ExperimentalAnimationApi::class)
class RoadmapComposeActivity : ComponentActivity() {
    
    private val viewModel: RoadmapViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            InTrainTheme {
                RoadmapComposeContent(
                    viewModel = viewModel,
                    onNavigateBack = { finish() },
                    onOpenCourse = { courseId ->
                        openCourseDetail(courseId)
                    }
                )
            }
        }
    }
    
    private fun openCourseDetail(courseId: String) {
        val intent = Intent(this, DetailCourseActivity::class.java).apply {
            putExtra("course_id", courseId)
        }
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RoadmapComposeContent(
    viewModel: RoadmapViewModel,
    onNavigateBack: () -> Unit,
    onOpenCourse: (String) -> Unit
) {
    var currentScreen by remember { mutableStateOf(RoadmapScreen.Main) }
    var selectedRoadmapId by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { if (targetState > initialState) it else -it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)) with
                slideOutHorizontally(
                    targetOffsetX = { if (targetState > initialState) -it else it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { screen ->
            when (screen) {
                RoadmapScreen.Main -> {
                    RoadmapScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { roadmapId ->
                            selectedRoadmapId = roadmapId
                            currentScreen = RoadmapScreen.Detail
                        },
                        onNavigateToProgress = { roadmapId ->
                            selectedRoadmapId = roadmapId
                            currentScreen = RoadmapScreen.Progress
                        },
                        onNavigateBack = onNavigateBack
                    )
                }
                
                RoadmapScreen.Detail -> {
                    selectedRoadmapId?.let { roadmapId ->
                        RoadmapDetailScreen(
                            viewModel = viewModel,
                            roadmapId = roadmapId,
                            onNavigateBack = {
                                currentScreen = RoadmapScreen.Main
                                selectedRoadmapId = null
                            },
                            onStartRoadmap = {
                                currentScreen = RoadmapScreen.Progress
                            },
                            onOpenCourse = onOpenCourse
                        )
                    }
                }
                
                RoadmapScreen.Progress -> {
                    selectedRoadmapId?.let { roadmapId ->
                        RoadmapProgressScreen(
                            viewModel = viewModel,
                            roadmapId = roadmapId,
                            onNavigateBack = {
                                currentScreen = RoadmapScreen.Main
                                selectedRoadmapId = null
                            },
                            onOpenCourse = onOpenCourse
                        )
                    }
                }
            }
        }
    }
}

enum class RoadmapScreen {
    Main,
    Detail,
    Progress
} 