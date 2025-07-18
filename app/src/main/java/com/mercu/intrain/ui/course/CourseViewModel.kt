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

class EnrollCourseViewModel : ViewModel() {

    // --- DATA UNTUK "ENROLLED" (IN-PROGRESS) ---
    private val _enrolledCourses = MutableLiveData<List<EnrollmentItem>>()
    val enrolledCourses: LiveData<List<EnrollmentItem>> get() = _enrolledCourses

    // --- DATA UNTUK "COMPLETED" ---
    private val _completedCourses = MutableLiveData<List<EnrollmentItem>>()
    val completedCourses: LiveData<List<EnrollmentItem>> get() = _completedCourses

    // --- DATA DETAIL & STATE BERSAMA ---
    private val _courseDetails = MutableLiveData<Course?>()
    val courseDetails: LiveData<Course?> get() = _courseDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _emptyMessage = MutableLiveData<String?>()
    val emptyMessage: LiveData<String?> get() = _emptyMessage

    fun fetchEnrolledCourse(userId: String) {
        fetchUserEnrollments(userId, isCompleted = false)
    }

    fun fetchCompletedCourses(userId: String) {
        fetchUserEnrollments(userId, isCompleted = true)
    }

    private fun fetchUserEnrollments(userId: String, isCompleted: Boolean) {
        _isLoading.value = true
        _emptyMessage.value = null // Reset pesan setiap kali fetch
        viewModelScope.launch {
            try {
                val response = ApiConfig.api.getUserEnrollments(userId)
                if (response.isSuccessful) {
                    val allEnrollments = response.body() ?: emptyList()
                    // Filter berdasarkan status
                    val filteredList = allEnrollments.filter { it.isCompleted == isCompleted }

                    if (filteredList.isEmpty()) {
                        val message = if (isCompleted) "You have not completed any courses yet." else "You haven't enrolled in any course yet."
                        _emptyMessage.postValue(message)
                    }

                    // Post ke LiveData yang sesuai
                    if (isCompleted) {
                        _completedCourses.postValue(filteredList)
                    } else {
                        _enrolledCourses.postValue(filteredList)
                    }
                } else {
                    _emptyMessage.postValue("Failed to load courses.")
                }
            } catch (e: Exception) {
                Log.e("EnrollCourseViewModel", "Exception: ${e.message}")
                _emptyMessage.postValue("An error occurred.")
            } finally {
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