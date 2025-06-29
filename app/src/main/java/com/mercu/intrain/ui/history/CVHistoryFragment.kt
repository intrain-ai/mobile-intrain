package com.mercu.intrain.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mercu.intrain.databinding.FragmentCvHistoryBinding

class CVHistoryFragment : Fragment() {
    private var _binding: FragmentCvHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by activityViewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCvHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(onCVClick = { cvReview ->
            val dialog = CVReviewDetailDialogFragment.newInstance(cvReview)
            dialog.show(parentFragmentManager, "cv_review_detail")
        })
        recyclerView.adapter = adapter
        viewModel.cvReviews.observe(viewLifecycleOwner) { list ->
            adapter.submitCVList(list)
        }
        viewModel.loadAllHistories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 