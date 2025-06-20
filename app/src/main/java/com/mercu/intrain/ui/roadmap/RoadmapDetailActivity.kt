package com.mercu.intrain.ui.roadmap

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
        binding = ActivityRoadmapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.loadRoadmapDetails(roadmapId)
    }

    private fun setupRecyclerView() {
        adapter = StepAdapter()
        binding.rvSteps.layoutManager = LinearLayoutManager(this)
        binding.rvSteps.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.roadmapDetails.observe(this) { roadmap ->
            binding.tvTitle.text = roadmap.title
            binding.tvDescription.text = roadmap.description
            binding.tvJobType.text = roadmap.jobType
            adapter.submitList(roadmap.steps)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnStart.setOnClickListener {
            viewModel.startRoadmap(roadmapId) {
                Toast.makeText(this, "Roadmap started!", Toast.LENGTH_SHORT).show()
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