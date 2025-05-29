package com.mercu.intrain.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.R
import com.mercu.intrain.sharedpref.SharedPrefHelper
import com.mercu.intrain.ui.profile.ProfileViewModel
import com.mercu.intrain.viewmodel.EnrollCourseViewModel
import kotlinx.coroutines.launch

class EnrolledCoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var adapter: EnrolledCoursesAdapter
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var viewModel: EnrollCourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_courses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(EnrollCourseViewModel::class.java)
        recyclerView = view.findViewById(R.id.recyclerView)
        textEmpty = view.findViewById(R.id.emptyView)
        sharedPrefHelper = SharedPrefHelper(requireContext())

        val userId = sharedPrefHelper.getUid().toString()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = EnrolledCoursesAdapter(emptyList()) { course ->
            Toast.makeText(requireContext(), "Klik: ${course.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        Log.d("EnrolledCoursesFragment", "onViewCreated: View is initialized")
        Log.d("EnrolledCoursesFragment", "textEmpty initialized: ${::textEmpty.isInitialized}")

        viewModel.fetchEnrolledCourse(userId)
    }
}

