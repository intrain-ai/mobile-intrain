package com.mercu.intrain.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mercu.intrain.databinding.FragmentHomeBinding

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

        // Observe the ViewModel's data and update the TextViews accordingly
        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.helloText.text = it // Update the "Hello, Aldi!" TextView
        }

        homeViewModel.courseCompleted.observe(viewLifecycleOwner) {
            binding.courseCompleted.text = it.toString() // Update the course completed count TextView
        }

        homeViewModel.courseDescription.observe(viewLifecycleOwner) {
            binding.courseDescription.text = it // Update the course description TextView
        }

        homeViewModel.activityContent.observe(viewLifecycleOwner) {
            binding.activityLabel.text = it // Update the activity label TextView
        }

        homeViewModel.newsContent.observe(viewLifecycleOwner) {
            binding.newsContent.text = it // Update the news content TextView
        }

        // Example of updating the activity content when an icon is clicked
        binding.cvCheckerIcon.setOnClickListener {
            homeViewModel.updateActivity("CV Checker Clicked") // Update activity content when clicked
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks by clearing the binding reference
    }
}
