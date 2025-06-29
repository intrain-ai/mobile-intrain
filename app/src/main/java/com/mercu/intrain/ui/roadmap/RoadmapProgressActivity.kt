package com.mercu.intrain.ui.roadmap//package com.mercu.intrain.ui.roadmap
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.mercu.intrain.databinding.ActivityRoadmapProgressBinding
//import com.mercu.intrain.model.ProgressStep
//
//class RoadmapProgressActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityRoadmapProgressBinding
//    private val viewModel: RoadmapViewModel by viewModels()
//    private lateinit var adapter: ProgressStepAdapter
//
//    private val roadmapId: String by lazy {
//        intent.getStringExtra(EXTRA_ROADMAP_ID) ?: throw IllegalStateException("Roadmap ID required")
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Enable edge-to-edge display
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        binding = ActivityRoadmapProgressBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupSystemUI()
//        setupToolbar()
//        setupRecyclerView()
//        setupObservers()
//        setupListeners()
//
//        viewModel.loadRoadmapProgress(roadmapId)
//    }
//
//    private fun setupSystemUI() {
//        // Make the status bar transparent and icons dark
//        window.statusBarColor = android.graphics.Color.TRANSPARENT
//        WindowInsetsControllerCompat(window, window.decorView).apply {
//            isAppearanceLightStatusBars = true
//        }
//    }
//
//    private fun setupToolbar() {
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//
//        binding.toolbar.setNavigationOnClickListener {
//            onBackPressed()
//        }
//    }
//
//    private fun setupRecyclerView() {
//        adapter = ProgressStepAdapter { step ->
//            if (!step.completed) {
//                viewModel.completeStep(roadmapId, step.id)
//            }
//        }
//        binding.rvProgressSteps.layoutManager = LinearLayoutManager(this)
//        binding.rvProgressSteps.adapter = adapter
//    }
//
//    private fun setupObservers() {
//        viewModel.roadmapProgress.observe(this) { steps ->
//            if (steps != null) {
//                adapter.submitList(steps)
//                updateProgress(steps)
//            } else {
//                adapter.submitList(emptyList())
//                updateProgress(emptyList())
//            }
//        }
//
//        viewModel.isLoading.observe(this) { isLoading ->
//            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
//            binding.btnContinue.isEnabled = isLoading != true
//            binding.btnDelete.isEnabled = isLoading != true
//        }
//
//        viewModel.errorMessage.observe(this) { error ->
//            if (!error.isNullOrEmpty()) {
//                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun setupListeners() {
//        binding.btnContinue.setOnClickListener {
//            // Navigate to the next incomplete step or show completion message
//            val steps = adapter.currentList
//            val nextIncompleteStep = steps.find { !it.completed }
//
//            if (nextIncompleteStep != null) {
//                // Navigate to the step (you can implement this based on your navigation)
//                Toast.makeText(this, "Continue to: ${nextIncompleteStep.title}", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Congratulations! You've completed all steps!", Toast.LENGTH_LONG).show()
//            }
//        }
//
//        binding.btnDelete.setOnClickListener {
//            // Show confirmation dialog before deleting
//            showDeleteConfirmationDialog()
//        }
//    }
//
//    private fun updateProgress(steps: List<ProgressStep>) {
//        val completed = steps.count { it.completed }
//        val total = steps.size
//        val progress = if (total > 0) (completed * 100) / total else 0
//
//        // Update progress indicators
//        binding.progressBarHorizontal.progress = progress
//        binding.tvProgress.text = "$completed/$total steps completed"
//        binding.tvPercentage.text = "$progress%"
//
//        // Update button states based on progress
//        binding.btnContinue.isEnabled = completed < total
//        binding.btnContinue.text = if (completed == total) "All Steps Completed!" else "Continue Learning"
//    }
//
//    private fun showDeleteConfirmationDialog() {
//        androidx.appcompat.app.AlertDialog.Builder(this)
//            .setTitle("Delete Learning Path")
//            .setMessage("Are you sure you want to delete this learning path? This action cannot be undone.")
//            .setPositiveButton("Delete") { _, _ ->
//                viewModel.deleteRoadmap(roadmapId)
//                Toast.makeText(this, "Learning path deleted", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }
//
//    companion object {
//        const val EXTRA_ROADMAP_ID = "extra_roadmap_id"
//
//        fun newIntent(context: Context, roadmapId: String) = Intent(context, RoadmapProgressActivity::class.java).apply {
//            putExtra(EXTRA_ROADMAP_ID, roadmapId)
//        }
//    }
//}