package com.mercu.intrain.ui.course

import android.os.Bundle
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
    private val apiService = ApiConfig.api
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_courses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        textEmpty = view.findViewById(R.id.textEmpty)
        sharedPrefHelper = SharedPrefHelper(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CourseAdapter(
            courseList = emptyList(),
            enrollList = emptyList()) { course ->
            Toast.makeText(requireContext(), "Klik: ${course.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        fetchEnrolledCourse()
    }

    private fun fetchEnrolledCourse() {
        val userId = sharedPrefHelper.getUid()
        lifecycleScope.launch {
            try {
                val response = apiService.getUserEnrollments(userId.toString())
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    adapter.updateDataEnroll(data)

                    if (::textEmpty.isInitialized) {
                        textEmpty.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
                    }

                } else {
                    if (::textEmpty.isInitialized) {
                        textEmpty.visibility = View.VISIBLE
                        textEmpty.text = "Gagal memuat course"
                    }
                }
            } catch (e: Exception) {
                if (::textEmpty.isInitialized) {
                    textEmpty.visibility = View.VISIBLE
                    textEmpty.text = "Kesalahan: ${e.message}"
                }
            }
        }
    }

}

