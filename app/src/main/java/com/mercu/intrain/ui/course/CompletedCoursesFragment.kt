package com.mercu.intrain.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.R
import com.mercu.intrain.repository.CourseRepository

class CompletedCoursesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var courseAdapter: CourseAdapter
    private lateinit var courseRepository: CourseRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        // Get courseRepository from parent activity
        val activity = requireActivity() as CourseActivity
        courseRepository = activity.courseRepository
        
        // Initialize adapter with completed courses
        courseAdapter = CourseAdapter(
            activity.completedCourses.toMutableList(),
            activity.courseDetails,
            courseRepository,
            viewLifecycleOwner
        )
        recyclerView.adapter = courseAdapter
    }

    fun updateAdapter() {
        courseAdapter.notifyDataSetChanged()
    }
} 