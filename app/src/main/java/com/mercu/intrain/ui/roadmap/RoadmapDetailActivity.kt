package com.mercu.intrain.ui.roadmap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mercu.intrain.databinding.ActivityRoadmapDetailBinding

class RoadmapDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoadmapDetailBinding
    private val viewModel: RoadmapViewModel by viewModels()
    private lateinit var adapter: StepAdapter

    private val roadmapId: String by lazy {
        intent.getStringExtra(EXTRA_ROADMAP_ID) ?: throw IllegalStateException("Roadmap ID required")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        binding = ActivityRoadmapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.loadRoadmapDetails(roadmapId)
    }

    private fun setupSystemUI() {
        // Make the status bar transparent and icons dark
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    private fun setupRecyclerView() {
        adapter = StepAdapter()
        binding.rvSteps.layoutManager = LinearLayoutManager(this)
        binding.rvSteps.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.roadmapDetails.observe(this) { roadmap ->
            if (roadmap != null) {
                // Update header information
                binding.tvTitle.text = roadmap.title ?: "Unknown Roadmap"
                binding.tvDescription.text = roadmap.description ?: "No description available"
                binding.tvJobType.text = roadmap.jobType ?: "Unknown Type"
                
                // Update steps list
                adapter.submitList(roadmap.steps ?: emptyList())
            } else {
                // Handle null roadmap
                binding.tvTitle.text = "Roadmap Not Found"
                binding.tvDescription.text = "The requested roadmap could not be loaded."
                binding.tvJobType.text = "Unknown"
                adapter.submitList(emptyList())
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
            binding.btnStart.isEnabled = isLoading != true
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnStart.setOnClickListener {
            binding.btnStart.isEnabled = false
            viewModel.startRoadmap(roadmapId) {
                Toast.makeText(this, "Roadmap started successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ROADMAP_ID = "extra_roadmap_id"

        fun newIntent(context: Context, roadmapId: String) = Intent(context, RoadmapDetailActivity::class.java).apply {
            putExtra(EXTRA_ROADMAP_ID, roadmapId)
        }
    }
}