package com.mercu.intrain.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.chat.DiffSelectActivity
import com.mercu.intrain.ui.course.CourseActivity
import com.mercu.intrain.ui.cvcheck.ReviewComposeActivity
import com.mercu.intrain.ui.roadmap.RoadmapComposeActivity
import com.mercu.intrain.ui.theme.InTrainTheme

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                InTrainTheme {
                    HomeScreen(
                        viewModel = viewModel(
                            factory = HomeViewModelFactory(
                                SharedPrefHelper(requireContext()),
                                ApiConfig.api
                            )
                        ),
                        onCourseClick = {
                            startActivity(Intent(requireContext(), CourseActivity::class.java))
                        },
                        onCVCheckClick = {
                            startActivity(Intent(requireContext(), ReviewComposeActivity::class.java))
                        },
                        onChatBotClick = {
                            startActivity(Intent(requireContext(), DiffSelectActivity::class.java))
                        },
                        onNavigateToRoadmapDetail = { roadmapId ->
                            val intent = Intent(requireContext(), RoadmapComposeActivity::class.java).apply {
                                putExtra("roadmap_id", roadmapId)
                                putExtra("screen", "progress")
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
