package com.mercu.intrain.ui.home

import android.R.attr.text
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.databinding.FragmentHomeBinding
import com.mercu.intrain.ui.chat.DiffSelectActivity
import com.mercu.intrain.ui.course.CourseActivity
import com.mercu.intrain.ui.cvcheck.ReviewActivity
import com.mercu.intrain.ui.news.NewsAdapter
import com.mercu.intrain.API.Article
import com.mercu.intrain.R
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.custom.CarouselItemDecoration
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var sharedPrefHelper : SharedPrefHelper
    private var sliderHandler: Handler? = null
    private var sliderRunnable: Runnable? = null
    private var currentItem = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPrefHelper = SharedPrefHelper(requireContext())
        val factory = HomeViewModelFactory(sharedPrefHelper)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefHelper = SharedPrefHelper(requireContext())

        setupUI()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        newsAdapter = NewsAdapter()
        binding.newsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
            PagerSnapHelper().attachToRecyclerView(this)
            addItemDecoration(CarouselItemDecoration(resources.getDimensionPixelSize(R.dimen.item_margin)))
            addOnScrollListener(carouselScrollListener)
        }
    }


    private fun setupObservers() {
        homeViewModel.apply {
            name.observe(viewLifecycleOwner) { binding.helloText.text = it }
            courseDescription.observe(viewLifecycleOwner) { binding.courseDescription.text = it }
            courseProgress.observe(viewLifecycleOwner) {
                binding.courseProgress.progress = it.toInt()
                binding.progressText.text = "${it.toInt()}%"
            }
            activityContent.observe(viewLifecycleOwner) { binding.activityLabel.text = it }
            newsArticles.observe(viewLifecycleOwner) { articles ->
                newsAdapter.submitList(articles ?: emptyList())
                setupAutoScroll(articles?.size ?: 0)
            }
        }
        homeViewModel.loadNews()
    }

    private fun setupClickListeners() {
        binding.apply {
            cvCheckerIcon.setOnClickListener {
                startActivity(Intent(requireContext(), ReviewActivity::class.java))
            }

            inTrainIcon.setOnClickListener {
                homeViewModel.updateProgress(20f)
                startActivity(Intent(requireContext(), DiffSelectActivity::class.java))
            }

            courseIcon.setOnClickListener {
                homeViewModel.updateProgress(80f)
                startActivity(Intent(requireContext(), CourseActivity::class.java))
            }
        }
    }

    private val carouselScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val centerX = recyclerView.width / 2
            recyclerView.children.forEach { child ->
                val childCenterX = child.left + (child.width / 2)
                val distance = abs(centerX - childCenterX)
                val scale = 1.0f - (distance.toFloat() / centerX * 0.15f)
                val alpha = 0.5f + (scale * 0.5f)

                child.scaleX = scale
                child.scaleY = scale
                child.alpha = alpha

                // Parallax effect for image
                child.findViewById<View>(R.id.newsImage).translationY = -(distance * 0.2f)
            }
        }
    }

    private fun setupAutoScroll(itemCount: Int) {
        if (itemCount <= 0) return // Prevent auto-scroll for empty list
        
        sliderHandler = Handler(Looper.getMainLooper())
        sliderRunnable = object : Runnable {
            override fun run() {
                if (_binding != null) {
                    currentItem = (currentItem + 1) % itemCount
                    binding.newsRecyclerView.smoothScrollToPosition(currentItem)
                    sliderHandler?.postDelayed(this, 4000)
                }
            }
        }
        sliderHandler?.postDelayed(sliderRunnable!!, 4000)
    }

    override fun onDestroyView() {
        sliderRunnable?.let { runnable ->
            sliderHandler?.removeCallbacks(runnable)
        }
        sliderHandler = null
        sliderRunnable = null
        super.onDestroyView()
        _binding = null
    }

    class CarouselItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            outRect.left = if (position == 0) margin else margin / 2
            outRect.right = if (position == (parent.adapter?.itemCount?.minus(1) ?: 0)) margin else margin / 2
        }
    }
}