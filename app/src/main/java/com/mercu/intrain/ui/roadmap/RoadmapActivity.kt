package com.mercu.intrain.ui.roadmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mercu.intrain.databinding.ActivityRoadmapBinding

class RoadmapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoadmapBinding
    private val viewModel: RoadmapViewModel by viewModels()
    private lateinit var adapter: RoadmapAdapter
    private lateinit var userRoadmapAdapter: UserRoadmapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoadmapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = RoadmapAdapter { roadmap ->
            startActivity(RoadmapDetailActivity.newIntent(this, roadmap.id))
        }
        binding.rvRoadmaps.layoutManager = LinearLayoutManager(this)
        binding.rvRoadmaps.adapter = adapter

        userRoadmapAdapter = UserRoadmapAdapter { userRoadmap ->
            startActivity(RoadmapProgressActivity.newIntent(this, userRoadmap.roadmapId))
        }
        binding.rvUserRoadmaps.layoutManager = LinearLayoutManager(this)
        binding.rvUserRoadmaps.adapter = userRoadmapAdapter
    }

    private fun setupObservers() {
        viewModel.roadmaps.observe(this) { roadmaps ->
            adapter.submitList(roadmaps)
            binding.tvEmpty.visibility = if (roadmaps.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.userRoadmaps.observe(this) { userRoadmaps ->
            android.util.Log.d("RoadmapActivity", "Received user roadmaps: ${userRoadmaps.size}")
            userRoadmapAdapter.submitList(userRoadmaps)
            binding.tvEmptyUserRoadmaps.visibility = if (userRoadmaps.isEmpty()) View.VISIBLE else View.GONE
            binding.rvUserRoadmaps.visibility = if (userRoadmaps.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefresh.isRefreshing = isLoading
            binding.btnMyRoadmaps.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                android.util.Log.e("RoadmapActivity", "Error: $error")
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }

        binding.btnMyRoadmaps.setOnClickListener {
            binding.btnMyRoadmaps.isEnabled = false
            viewModel.loadUserRoadmaps()
        }
    }

    private fun loadData() {
        viewModel.loadAllRoadmaps()
        viewModel.loadUserRoadmaps()
    }

    override fun onResume() {
        super.onResume()
        binding.btnMyRoadmaps.isEnabled = true
    }
}