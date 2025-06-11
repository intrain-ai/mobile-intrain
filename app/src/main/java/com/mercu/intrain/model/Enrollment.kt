package com.mercu.intrain.model

import com.google.gson.annotations.SerializedName

data class Enrollment(

	@field:SerializedName("Enrollment")
	val enrollment: List<EnrollmentItem?>? = null
)

data class EnrollmentItem(

	@field:SerializedName("completed_at")
	val completedAt: Any? = null,

	@field:SerializedName("course_id")
	val courseId: String? = null,

	@field:SerializedName("enrolled_at")
	val enrolledAt: String? = null,

	@field:SerializedName("provider")
	val provider: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("course_title")
	val courseTitle: String? = null,

	@field:SerializedName("enrolled_status")
	val enrolledStatus: Boolean? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("is_completed")
	val isCompleted: Boolean? = null
)
