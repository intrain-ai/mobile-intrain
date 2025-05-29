package com.mercu.intrain.API

import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import com.mercu.intrain.model.EnrollmentRequest
import com.mercu.intrain.model.EnrollmentResponse
import com.mercu.intrain.model.WorkExperience
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

    @PUT("api/v1/auth/user/update")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<UpdateUserResponse>

    //Profle Work Experience

    //Create
    @POST("api/v1/users/{userId}/work_experiences")
    suspend fun createWorkExperience(
        @Path("userId") userId: String,
        @Body workExperience: WorkExperience
    ): Response<WorkExperience>

    //Ambil Work Experience
    @GET("api/v1/users/{userId}/work_experiences")
    suspend fun getWorkExperiences(
        @Path("userId") userId: String
    ): Response<List<WorkExperience>>

    @GET("api/v1/users/{userId}/work_experiences/{expId}")
    suspend fun getWorkExperienceDetail(
        @Path("userId") userId: String,
        @Path("expId") expId: String
    ): Response<WorkExperience>

    @PUT("api/v1/users/{userId}/work_experiences/{expId}")
    suspend fun updateWorkExperience(
        @Path("userId") userId: String,
        @Path("expId") expId: String,
        @Body workExperience: WorkExperience
    ): Response<WorkExperience>

    @DELETE("api/v1/users/{userId}/work_experiences/{expId}")
    suspend fun deleteWorkExperience(
        @Path("userId") userId: String,
        @Path("expId") expId: String
    ): Response<Unit>

    //News
    @GET("v2/everything")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = "bf4374ec295e42a99952261bef02bbb9",
        @Query("language") language: String = "id",
        @Query("pageSize") pageSize: Int = 5
    ): Response<NewsResponse>

}