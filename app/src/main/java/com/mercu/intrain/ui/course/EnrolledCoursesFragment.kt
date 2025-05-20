package com.mercu.intrain.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.R
import com.mercu.intrain.model.Course
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.launch

class EnrolledCoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var adapter: CourseAdapter
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_courses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views AFTER the view is created
        recyclerView = view.findViewById(R.id.recyclerView)
        textEmpty = view.findViewById(R.id.emptyView)
        sharedPrefHelper = SharedPrefHelper(requireContext())

        // Setup RecyclerView and Adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CourseAdapter(
            courseList = emptyList(),
            enrollList = emptyList()) { course ->
            Toast.makeText(requireContext(), "Klik: ${course.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Debug logs to check initialization of views
        Log.d("EnrolledCoursesFragment", "onViewCreated: View is initialized")
        Log.d("EnrolledCoursesFragment", "textEmpty initialized: ${::textEmpty.isInitialized}")

        // Fetch Enrolled Courses
        fetchEnrolledCourse()
    }

    private fun fetchEnrolledCourse() {
        val userId = sharedPrefHelper.getUid()
        lifecycleScope.launch {
            try {
                val response = ApiConfig.api.getUserEnrollments(userId.toString())
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    Log.d("EnrolledCoursesFragment", "enrollList size: ${data.size}")
                    adapter.updateDataEnroll(data)

                    if (data.isEmpty()) {
                        textEmpty.visibility = View.VISIBLE
                        textEmpty.text = "No courses enrolled"
                    } else {
                        textEmpty.visibility = View.GONE
                    }
                } else {
                    textEmpty.visibility = View.VISIBLE
                    textEmpty.text = "Gagal memuat course"
                }
            } catch (e: Exception) {
                textEmpty.visibility = View.VISIBLE
                textEmpty.text = "Kesalahan: ${e.message}"
            }
        }
    }
}

