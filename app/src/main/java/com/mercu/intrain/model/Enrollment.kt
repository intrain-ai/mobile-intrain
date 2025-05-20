package com.mercu.intrain.model

data class Enrollment(
    val id: String,
    val userId: String,
    val courseId: String,
    val status: String,
    val progress: Int,
    val enrolledAt: String,
    val completedAt: String?,
    val course: Course
)
