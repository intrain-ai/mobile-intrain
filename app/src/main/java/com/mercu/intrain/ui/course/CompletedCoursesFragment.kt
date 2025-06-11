package com.mercu.intrain.ui.course

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.R
import com.mercu.intrain.model.Course
import com.mercu.intrain.sharedpref.SharedPrefHelper

class CompletedCoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: EnrolledCoursesAdapter
    private lateinit var viewModel: EnrollCourseViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_courses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(EnrollCourseViewModel::class.java)
        sharedPrefHelper = SharedPrefHelper(requireContext())

        recyclerView = view.findViewById(R.id.recyclerView)
        textEmpty = view.findViewById(R.id.textEmpty)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        val userId = sharedPrefHelper.getUid().toString()
        viewModel.fetchCompletedCourses(userId)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = EnrolledCoursesAdapter(emptyList()) { enrollmentItem ->
            enrollmentItem.courseId?.let { id ->
                viewModel.fetchCourseDetailsById(id)
            } ?: Log.e("CompletedFragment", "Course ID is null")
        }
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.completedCourses.observe(viewLifecycleOwner) { courses ->
            adapter.updateDataComplete(courses)
        }

        viewModel.courseDetails.observe(viewLifecycleOwner) { course ->
            course?.let { navigateToDetail(it) }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.emptyMessage.observe(viewLifecycleOwner) { message ->
            textEmpty.visibility = if (message != null) View.VISIBLE else View.GONE
            textEmpty.text = message
        }
    }

    private fun navigateToDetail(course: Course) {
        val intent = Intent(requireContext(), DetailCourseActivity::class.java).apply {
            putExtra("course", course)
            putExtra("IS_ENROLLED", true)
            putExtra("IS_COMPLETED", true)
        }
        startActivity(intent)
        viewModel.onCourseDetailsNavigated()
    }
}