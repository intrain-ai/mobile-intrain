package com.mercu.intrain.viewmodel

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.ui.course.EnrolledCoursesAdapter
import kotlinx.coroutines.launch

private lateinit var adapter: EnrolledCoursesAdapter
private lateinit var textEmpty: TextView

class EnrollCourseViewModel : ViewModel(){

    fun fetchEnrolledCourse(userid : String) {
        val uid = userid
        viewModelScope.launch {
            try {
                val response = ApiConfig.api.getUserEnrollmentsMK(uid)
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
