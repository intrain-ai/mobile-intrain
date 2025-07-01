package com.mercu.intrain.ui.jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mercu.intrain.ui.theme.InTrainTheme

class JobsFragment : Fragment() {
    private val viewModel: JobViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                InTrainTheme {
                    JobsListScreen(viewModel = viewModel)
                }
            }
        }
    }
} 