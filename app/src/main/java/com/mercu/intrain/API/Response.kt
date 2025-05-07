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
data class ResgiterResponse(
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

