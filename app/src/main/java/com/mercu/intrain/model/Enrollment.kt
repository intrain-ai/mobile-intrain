package com.mercu.intrain.model

data class Enrollment(
    val id: String,
    val user_id: String,
    val course_id: String,
    val enrolled_at: String,
    val completed_at: String?,
    val is_completed: Boolean
)
