package com.mercu.intrain.ui.roadmap//package com.mercu.intrain.ui.roadmap
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.mercu.intrain.databinding.ActivityRoadmapBinding
//
//class RoadmapActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityRoadmapBinding
//    private val viewModel: RoadmapViewModel by viewModels()
//    private lateinit var adapter: RoadmapAdapter
//    private lateinit var userRoadmapAdapter: UserRoadmapAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Enable edge-to-edge display
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        binding = ActivityRoadmapBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupSystemUI()
//        setupToolbar()
//        setupRecyclerView()
//        setupObservers()
//        setupListeners()
//        loadData()
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
//        adapter = RoadmapAdapter { roadmap ->
//            startActivity(RoadmapDetailActivity.newIntent(this, roadmap.id))
//        }
//        binding.rvRoadmaps.layoutManager = LinearLayoutManager(this)
//        binding.rvRoadmaps.adapter = adapter
//
//        userRoadmapAdapter = UserRoadmapAdapter { userRoadmap ->
//            startActivity(RoadmapProgressActivity.newIntent(this, userRoadmap.roadmapId))
//        }
//        binding.rvUserRoadmaps.layoutManager = LinearLayoutManager(this)
//        binding.rvUserRoadmaps.adapter = userRoadmapAdapter
//    }
//
//    private fun setupObservers() {
//        viewModel.roadmaps.observe(this) { roadmaps ->
//            android.util.Log.d("RoadmapActivity", "Available roadmaps: ${roadmaps?.size ?: 0}")
//            adapter.submitList(roadmaps ?: emptyList())
//            binding.tvEmpty.visibility = if (roadmaps.isNullOrEmpty()) View.VISIBLE else View.GONE
//        }
//
//        viewModel.userRoadmaps.observe(this) { userRoadmaps ->
//            android.util.Log.d("RoadmapActivity", "User roadmaps: ${userRoadmaps?.size ?: 0}")
//            userRoadmapAdapter.submitList(userRoadmaps ?: emptyList())
//            binding.tvEmptyUserRoadmaps.visibility = if (userRoadmaps.isNullOrEmpty()) View.VISIBLE else View.GONE
//            binding.rvUserRoadmaps.visibility = if (userRoadmaps.isNullOrEmpty()) View.GONE else View.VISIBLE
//
//            // Debug: Log RecyclerView state
//            android.util.Log.d("RoadmapActivity", "RecyclerView visibility: ${binding.rvUserRoadmaps.visibility}")
//            android.util.Log.d("RoadmapActivity", "RecyclerView adapter item count: ${userRoadmapAdapter.itemCount}")
//        }
//
//        viewModel.isLoading.observe(this) { isLoading ->
//            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
//            binding.swipeRefresh.isRefreshing = isLoading == true
//            binding.btnMyRoadmaps.isEnabled = isLoading != true
//        }
//
//        viewModel.errorMessage.observe(this) { error ->
//            if (!error.isNullOrEmpty()) {
//                android.util.Log.e("RoadmapActivity", "Error: $error")
//                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun setupListeners() {
//        binding.swipeRefresh.setOnRefreshListener {
//            loadData()
//        }
//
//        binding.btnMyRoadmaps.setOnClickListener {
//            binding.btnMyRoadmaps.isEnabled = false
//            viewModel.loadUserRoadmaps()
//        }
//    }
//
//    private fun loadData() {
//        viewModel.loadAllRoadmaps()
//        viewModel.loadUserRoadmaps()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.btnMyRoadmaps.isEnabled = true
//        // Refresh data when returning to the activity
//        loadData()
//    }
//}