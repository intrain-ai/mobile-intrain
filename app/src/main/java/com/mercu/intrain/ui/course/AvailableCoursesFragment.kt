package com.mercu.intrain.ui.course

import android.content.Intent
import android.os.Bundle
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

class AvailableCoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: CourseAdapter
    private lateinit var viewModel: AvailableCoursesViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_courses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AvailableCoursesViewModel::class.java)
        sharedPrefHelper = SharedPrefHelper(requireContext())

        recyclerView = view.findViewById(R.id.recyclerView)
        textEmpty = view.findViewById(R.id.textEmpty)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        setupObservers()

    }

    override fun onResume() {
        super.onResume()
        // Ambil data setiap kali fragment ini ditampilkan kembali
        val userId = sharedPrefHelper.getUid().toString()
        viewModel.fetchAllCourses()
        viewModel.fetchUserEnrollments(userId)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CourseAdapter(emptyList(), emptyList()) { course ->
            viewModel.fetchCourseDetailsById(course.id)
        }
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.availableCourses.observe(viewLifecycleOwner) { courses ->
            adapter.updateData(courses)
        }

        viewModel.userEnrollments.observe(viewLifecycleOwner) { enrollments ->
            adapter.updateUserEnrollments(enrollments)
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
        }
        startActivity(intent)
        viewModel.onCourseDetailsNavigated()
    }
}