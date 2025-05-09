package com.mercu.intrain.API

import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import com.mercu.intrain.model.EnrollmentRequest
import com.mercu.intrain.model.EnrollmentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/v1/auth/user/login")
    fun loginRequest(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/api/v1/auth/user/register")
    fun registerRequest(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @Multipart
    @POST("/api/v1/feature/cv/upload")
    suspend fun uploadPdfWithText(
        @Part file: MultipartBody.Part,
        @Part("user_id") userid: RequestBody
    ): Response<CvResponse>


    @POST("/api/v1/feature/interview/chat")
    fun chatRequestInit(
        @Body request: ChatRequest
    ): Call<ChatInitResponse>

    @POST("/api/v1/feature/interview/chat")
    fun chatRequestContinous(
        @Body request: ChatContinous
    ): Call<ResponseBody>

    // Course endpoints
    @GET("api/v1/feature/courses")
    suspend fun getAllCourses(): Response<List<Course>>

    @GET("api/v1/feature/courses/{courseId}")
    suspend fun getCourseDetails(@Path("courseId") courseId: String): Response<Course>

    @GET("api/v1/feature/courses/user/{userId}/enrollments")
    suspend fun getUserEnrollments(@Path("userId") userId: String): Response<List<Enrollment>>

    @POST("api/v1/feature/courses/enroll")
    suspend fun enrollCourse(@Body request: EnrollmentRequest): Response<EnrollmentResponse>

    @POST("api/v1/feature/courses/complete")
    suspend fun completeCourse(@Body request: EnrollmentRequest): Response<EnrollmentResponse>

    @POST("api/v1/feature/courses/unenroll")
    suspend fun unenrollCourse(@Body request: EnrollmentRequest): Response<Map<String, String>>
}