package com.mercu.intrain.API

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class LoginResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user")
    val user: User
) : Parcelable

@Parcelize
data class User(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("username")
    val username: String
) : Parcelable

@Parcelize
data class LoginRequest(
    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("password")
    val password: String? = null
) : Parcelable

// REGISTER

@Parcelize
data class RegisterResponse(
    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class RegisterRequest(

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("password")
    val email: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("email")
    val createdAt: String? = null,

    ) : Parcelable
@Parcelize
data class CVuploadResponse(
    @field:SerializedName("jawa")
    val jawa: String //dummy
) : Parcelable


data class ChatRequest(

    @field:SerializedName("job_type")
    val jobType: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("hr_level_id")
    val hrLevelId: Int? = null
)
// buat initialisasi
@Parcelize
data class ChatInitResponse(

    @field:SerializedName("response")
    val response: ChatResponse? = null,

    @field:SerializedName("session_id")
    val sessionId: String? = null
): Parcelable

@Parcelize
data class ChatResponse(

    @field:SerializedName("question_text")
    val questionText: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("question_number")
    val questionNumber: Int? = null
): Parcelable

data class ChatContinous(
    @field:SerializedName("session_id")
    val sessionId: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)

@Parcelize
data class ChatConResponse(

    @field:SerializedName("response")
    val response: ChatResponse? = null,

    @field:SerializedName("session_id")
    val sessionId: String? = null
): Parcelable

@Parcelize
data class EvaluationResponse(
    @SerializedName("evaluation")
    val evaluation: Evaluation? = null,

    @SerializedName("session_id")
    val sessionId: String? = null
) : Parcelable

@Parcelize
data class Evaluation(
    @SerializedName("evaluated_at")
    val evaluatedAt: String? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("recommendations")
    val recommendations: List<String>? = null,

    @SerializedName("score")
    val score: Int? = null,

    @SerializedName("session_id")
    val sessionId: String? = null
) : Parcelable

//CV UPLOAD

@Parcelize
data class CvResponse(

    @field:SerializedName("review")
    val review: Review? = null,

    @field:SerializedName("submission")
    val submission: Submission? = null,

    @field:SerializedName("sections")
    val sections: List<SectionsItem?>? = null
) : Parcelable

@Parcelize
data class Submission(

    @field:SerializedName("file_url")
    val fileUrl: String? = null,

    @field:SerializedName("uploaded_at")
    val uploadedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("file_name")
    val fileName: String? = null,

    @field:SerializedName("file_type")
    val fileType: String? = null,

    @field:SerializedName("id")
    val id: String? = null
) : Parcelable

@Parcelize
data class Review(

    @field:SerializedName("submission_id")
    val submissionId: String? = null,

    @field:SerializedName("ats_passed")
    val atsPassed: Boolean? = null,

    @field:SerializedName("reviewed_at")
    val reviewedAt: String? = null,

    @field:SerializedName("overall_feedback")
    val overallFeedback: String? = null,

    @field:SerializedName("id")
    val id: String? = null
) : Parcelable

@Parcelize
data class SectionsItem(

    @field:SerializedName("feedback")
    val feedback: String? = null,

    @field:SerializedName("review_id")
    val reviewId: String? = null,

    @field:SerializedName("needs_improvement")
    val needsImprovement: Boolean? = null,

    @field:SerializedName("section")
    val section: String? = null,

    @field:SerializedName("id")
    val id: String? = null
) : Parcelable

// Update Data
@Parcelize
data class UpdateUserRequest(
    @field:SerializedName("user_id")
    val user_id: String,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String

) : Parcelable

@Parcelize
data class UpdateUserResponse(
    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

//News
@Parcelize
data class NewsResponse(
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("totalResults")
    val totalResults: Int,
    @field:SerializedName("articles")
    val articles: List<Article>
) : Parcelable

@Parcelize
data class Article(
    @field:SerializedName("source")
    val source: Source,
    @field:SerializedName("author")
    val author: String?,
    @field:SerializedName("title")
    val title: String?,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("url")
    val url: String?,
    @field:SerializedName("urlToImage")
    val urlToImage: String?,
    @field:SerializedName("publishedAt")
    val publishedAt: String?
) : Parcelable

@Parcelize
data class Source(
    @field:SerializedName("id")
    val id: String?,
    @field:SerializedName("name")
    val name: String?
) : Parcelable

//Work Experience
@Parcelize
data class WorkExperience(
    @SerializedName("job_title")
    val jobTitle: String,

    @SerializedName("company_name")
    val companyName: String,

    @SerializedName("job_desc")
    val jobDescription: String,

    @SerializedName("start_month")
    val startMonth: Int,

    @SerializedName("start_year")
    val startYear: Int,

    @SerializedName("end_month")
    val endMonth: Int?, // Bisa null jika is_current true

    @SerializedName("end_year")
    val endYear: Int?,   // Bisa null jika is_current true

    @SerializedName("is_current")
    val isCurrent: Boolean
) : Parcelable

@Parcelize
data class Experience(
    @SerializedName("id")
    val id: String,
    @SerializedName("job_title")
    val jobTitle: String,
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("company_logo_url")
    val companyLogoUrl: String? = null,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("location")
    val location: String? = null
) : Parcelable