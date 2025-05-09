package com.mercu.intrain.model

data class EnrollmentResponse(
    val success: Boolean,
    val message: String,
    val enrollment: Enrollment?
) 