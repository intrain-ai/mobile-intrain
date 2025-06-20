package com.mercu.intrain.API

import com.google.gson.annotations.SerializedName

// Chat History Data Classes
data class ChatHistoryResponse(
    @SerializedName("chats") val chats: List<ChatSession>,
    @SerializedName("user_id") val userId: String
)

data class ChatSession(
    @SerializedName("hr_level_id") val hrLevelId: Int,
    @SerializedName("job_type") val jobType: String,
    @SerializedName("last_message") val lastMessage: String,
    @SerializedName("last_message_at") val lastMessageAt: String,
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("started_at") val startedAt: String
)

// Session History Data Classes
data class SessionHistoryResponse(
    @SerializedName("history") val history: List<ChatMessage>,
    @SerializedName("session_id") val sessionId: String
)

data class ChatMessage(
    @SerializedName("content") val content: MessageContent,
    @SerializedName("sender") val sender: String,
    @SerializedName("sent_at") val sentAt: String
)

data class MessageContent(
    @SerializedName("question_number") val questionNumber: Int?,
    @SerializedName("question_text") val questionText: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("text") val text: String?
)

// CV History Data Classes
data class CVSubmissionResponse(
    @SerializedName("submission") val submission: CVSubmission
)

data class CVSubmission(
    @SerializedName("id") val id: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_type") val fileType: String,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("uploaded_at") val uploadedAt: String,
    @SerializedName("user_id") val userId: String
)

data class CVReviewHistoryResponse(
    @SerializedName("review") val review: CVReview,
    @SerializedName("sections") val sections: List<ReviewSection>,
    @SerializedName("submission") val submission: CVSubmission
)

data class CVReview(
    @SerializedName("id") val id: String,
    @SerializedName("ats_passed") val atsPassed: Boolean,
    @SerializedName("overall_feedback") val overallFeedback: String,
    @SerializedName("reviewed_at") val reviewedAt: String,
    @SerializedName("submission_id") val submissionId: String
)

data class ReviewSection(
    @SerializedName("id") val id: String,
    @SerializedName("feedback") val feedback: String,
    @SerializedName("needs_improvement") val needsImprovement: Boolean,
    @SerializedName("review_id") val reviewId: String,
    @SerializedName("section") val section: String
)

data class CVListResponse(
    @SerializedName("id") val id: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_type") val fileType: String,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("uploaded_at") val uploadedAt: String,
    @SerializedName("user_id") val userId: String
)