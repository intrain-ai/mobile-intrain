package com.mercu.intrain.ui.course

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.EnrollmentItem
import kotlinx.coroutines.launch

class AvailableCoursesViewModel : ViewModel() {

    // LiveData untuk daftar semua kursus yang tersedia
    private val _availableCourses = MutableLiveData<List<Course>>()
    val availableCourses: LiveData<List<Course>> get() = _availableCourses

    // --- TAMBAHAN ---
    // LiveData untuk daftar kursus yang diikuti pengguna
    private val _userEnrollments = MutableLiveData<List<EnrollmentItem>>()
    val userEnrollments: LiveData<List<EnrollmentItem>> get() = _userEnrollments

    // LiveData untuk menampung hasil fetch detail course (digunakan saat item diklik)
    private val _courseDetails = MutableLiveData<Course?>()
    val courseDetails: LiveData<Course?> get() = _courseDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _emptyMessage = MutableLiveData<String?>()
    val emptyMessage: LiveData<String?> get() = _emptyMessage

    fun fetchAllCourses() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.api.getAllCourses()
                if (response.isSuccessful) {
                    val courses = response.body() ?: emptyList()
                    if (courses.isEmpty()) {
                        _emptyMessage.postValue("No courses available at the moment.")
                    } else {
                        _availableCourses.postValue(courses)
                        _emptyMessage.postValue(null)
                    }
                } else {
                    _emptyMessage.postValue("Failed to load courses.")
                }
            } catch (e: Exception) {
                Log.e("AvailableCoursesVM", "Exception fetching all courses: ${e.message}")
                _emptyMessage.postValue("An error occurred.")
            } finally {
                // Jangan set loading ke false di sini, tunggu fetch enrollments selesai
            }
        }
    }

    // --- FUNGSI TAMBAHAN ---
    fun fetchUserEnrollments(userId: String) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.api.getUserEnrollments(userId)
                if (response.isSuccessful) {
                    _userEnrollments.postValue(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                Log.e("AvailableCoursesVM", "Exception fetching enrollments: ${e.message}")
            } finally {
                // Set loading ke false setelah kedua data selesai diambil
                _isLoading.value = false
            }
        }
    }

    fun fetchCourseDetailsById(courseId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.api.getCourseDetails(courseId)
                if (response.isSuccessful) {
                    _courseDetails.postValue(response.body())
                } else {
                    _courseDetails.postValue(null)
                }
            } catch (e: Exception) {
                _courseDetails.postValue(null)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCourseDetailsNavigated() {
        _courseDetails.value = null
    }
}