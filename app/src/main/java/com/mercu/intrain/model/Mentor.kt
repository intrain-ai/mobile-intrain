package com.mercu.intrain.model

import com.google.gson.annotations.SerializedName

data class MentorProfile(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("expertise")
    val expertise: String,

    @SerializedName("bio")
    val bio: String,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("name")
    val name: String = ""
)

data class Availability(
    @SerializedName("id")
    val id: String,

    @SerializedName("mentor_id")
    val mentorId: String,

    @SerializedName("start_datetime")
    val startDatetime: String,

    @SerializedName("end_datetime")
    val endDatetime: String
)

data class MentorshipSession(
    @SerializedName("id")
    val id: String,

    @SerializedName("mentee_id")
    val menteeId: String,

    @SerializedName("mentor_id")
    val mentorId: String,

    @SerializedName("scheduled_at")
    val scheduledAt: String,

    @SerializedName("meet_link")
    val meetLink: String
)

data class MentorFeedback(
    @SerializedName("id")
    val id: String,

    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("feedback")
    val feedback: String
)

data class MentorProfileWithExperience(
    @SerializedName("mentor_profile")
    val mentorProfile: MentorProfile,

    @SerializedName("work_experiences")
    val workExperiences: List<WorkExperience>
)

data class RegisterMentorRequest(
    @SerializedName("user_id") val userId: String,
    val expertise: String,
    val bio: String
)

data class SetAvailabilityRequest(
    @SerializedName("start_datetime") val startDatetime: String,
    @SerializedName("end_datetime") val endDatetime: String
)

data class BookSessionRequest(
    @SerializedName("mentee_id") val menteeId: String,
    @SerializedName("availability_id") val availabilityId: String
)

data class SubmitFeedbackRequest(
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("feedback")
    val feedback: String
)
