package com.mercu.intrain.model

import com.google.gson.annotations.SerializedName

data class Feedback(

	@field:SerializedName("response")
	val response: FeedbackResponse? = null,

	@field:SerializedName("session_id")
	val sessionId: String? = null
)

data class FeedbackResponse(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("feedback_text")
	val feedbackText: String? = null
)
