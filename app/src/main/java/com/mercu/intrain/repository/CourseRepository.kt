package com.mercu.intrain.repository

import com.mercu.intrain.API.ApiService
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import com.mercu.intrain.model.EnrollmentItem
import com.mercu.intrain.model.EnrollmentRequest
import com.mercu.intrain.model.EnrollmentResponse
import retrofit2.Response

class CourseRepository(private val apiService: ApiService) {
    suspend fun getAllCourses(): Response<List<Course>> {
        return apiService.getAllCourses()
    }

    suspend fun getCourseDetails(courseId: String): Response<Course> {
        return apiService.getCourseDetails(courseId)
    }

    suspend fun getUserEnrollments(userId: String): Response<List<EnrollmentItem>> {
        return apiService.getUserEnrollments(userId)
    }

    suspend fun enrollCourse(request: EnrollmentRequest): Response<EnrollmentResponse> {
        return apiService.enrollCourse(request)
    }

    suspend fun completeCourse(request: EnrollmentRequest): Response<EnrollmentResponse> {
        return apiService.completeCourse(request)
    }

    suspend fun unenrollCourse(request: EnrollmentRequest): Response<Map<String, String>> {
        return apiService.unenrollCourse(request)
    }
} 
