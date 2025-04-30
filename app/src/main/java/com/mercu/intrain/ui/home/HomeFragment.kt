package com.mercu.intrain.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mercu.intrain.databinding.FragmentHomeBinding
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.widget.TextView
import com.mercu.intrain.MainActivity
import com.mercu.intrain.ui.chat.ChatActivity
import com.mercu.intrain.ui.cvcheck.ReviewActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the ViewModel
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Inflate the layout using ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observe the ViewModel's data and update the TextViews and progress bar accordingly
        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.helloText.text = it // Update the "Hello, Aldi!" TextView
        }

        homeViewModel.courseDescription.observe(viewLifecycleOwner) {
            binding.courseDescription.text = it // Update course description
        }

        homeViewModel.courseProgress.observe(viewLifecycleOwner) {
            // Set the progress dynamically based on the LiveData value (from 0 to 100)
            binding.courseProgress.progress = it.toInt() // Cast to Int for the progress value

            // Update the text in the center of the progress circle
            binding.progressText.text = "${it.toInt()}%" // Update percentage in the center
        }

        homeViewModel.activityContent.observe(viewLifecycleOwner) {
            binding.activityLabel.text = it // Update activity content
        }

        homeViewModel.newsContent.observe(viewLifecycleOwner) {
            binding.newsContent.text = it // Update news content
        }

        // Simulate progress increase (e.g., progress from 0% to 100% over time)
        // Example: Update the progress to 50% after 2 seconds
        binding.cvCheckerIcon.setOnClickListener {
            val intent = Intent(requireContext(), ReviewActivity::class.java)
            startActivity(intent)
        }

        binding.inTrainIcon.setOnClickListener {
            // You can update the progress dynamically like this:
            homeViewModel.updateProgress(20f) // Update progress to 20%
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }

        binding.courseIcon.setOnClickListener {
            // You can update the progress dynamically like this:
            homeViewModel.updateProgress(80f) // Update progress to 80%
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
