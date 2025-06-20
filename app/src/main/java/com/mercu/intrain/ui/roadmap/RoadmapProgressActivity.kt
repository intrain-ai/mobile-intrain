package com.mercu.intrain.ui.roadmap
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mercu.intrain.databinding.ActivityRoadmapProgressBinding
import com.mercu.intrain.model.ProgressStep

class RoadmapProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoadmapProgressBinding
    private val viewModel: RoadmapViewModel by viewModels()
    private lateinit var adapter: ProgressStepAdapter

    private val roadmapId: String by lazy {
        intent.getStringExtra(EXTRA_ROADMAP_ID) ?: throw IllegalStateException("Roadmap ID required")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoadmapProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.loadRoadmapProgress(roadmapId)
    }

    private fun setupRecyclerView() {
        adapter = ProgressStepAdapter { step ->
            if (!step.completed) {
                viewModel.completeStep(roadmapId, step.id)
            }
        }
        binding.rvProgressSteps.layoutManager = LinearLayoutManager(this)
        binding.rvProgressSteps.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.roadmapProgress.observe(this) { steps ->
            adapter.submitList(steps)
            updateProgress(steps)
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
        binding.btnDelete.setOnClickListener {
            viewModel.deleteRoadmap(roadmapId)
            finish()
        }
    }

    private fun updateProgress(steps: List<ProgressStep>) {
        val completed = steps.count { it.completed }
        val total = steps.size
        val progress = if (total > 0) (completed * 100) / total else 0

        binding.progressBarHorizontal.progress = progress
        binding.tvProgress.text = "$completed/$total steps completed"
        binding.tvPercentage.text = "$progress%"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ROADMAP_ID = "extra_roadmap_id"

        fun newIntent(context: Context, roadmapId: String) = Intent(context, RoadmapProgressActivity::class.java).apply {
            putExtra(EXTRA_ROADMAP_ID, roadmapId)
        }
    }
}