package com.mercu.intrain.API

import com.mercu.intrain.model.Availability
import com.mercu.intrain.model.CompletedStepResponse
import com.mercu.intrain.model.Course
//import com.mercu.intrain.model.EnrollMock
import com.mercu.intrain.model.EnrollmentItem
//import com.mercu.intrain.model.EnrollmentMockResponse
import com.mercu.intrain.model.EnrollmentRequest
import com.mercu.intrain.model.EnrollmentResponse
import com.mercu.intrain.model.MentorFeedback
import com.mercu.intrain.model.MentorProfile
import com.mercu.intrain.model.MentorProfileWithExperience
import com.mercu.intrain.model.MentorshipSession
import com.mercu.intrain.model.ProgressStep
import com.mercu.intrain.model.Roadmap
import com.mercu.intrain.model.UserRoadmap
import com.mercu.intrain.model.UserRoadmapHistory
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
    suspend fun getUserEnrollments(@Path("userId") userId: String): Response<List<EnrollmentItem>>

    @POST("api/v1/feature/courses/enroll")
    suspend fun enrollCourse(@Body request: EnrollmentRequest): Response<EnrollmentResponse>

    @POST("api/v1/feature/courses/complete")
    suspend fun completeCourse(@Body request: EnrollmentRequest): Response<EnrollmentResponse>

    @POST("api/v1/feature/courses/unenroll")
    suspend fun unenrollCourse(@Body request: EnrollmentRequest): Response<Map<String, String>>

    //History
// Chat Endpoints
    @GET("api/v1/feature/interview/chat/history/{user_id}")
    suspend fun getChatHistory(
        @Path("user_id") userId: String
    ): Response<ChatHistoryResponse>

    @GET("api/v1/feature/interview/chat/{session_id}/history")
    suspend fun getSessionHistory(
        @Path("session_id") sessionId: String
    ): Response<SessionHistoryResponse>

    // CV Endpoints
    @GET("api/v1/feature/cv/history/{submission_id}")
    suspend fun getCVSubmission(
        @Path("submission_id") submissionId: String
    ): Response<CVSubmissionResponse>

    @GET("api/v1/feature/cv/history/user/{user_id}/reviews")
    suspend fun getCVReviews(
        @Path("user_id") userId: String
    ): Response<List<CVReviewHistoryResponse>>

    @GET("api/v1/feature/cv/history/user/{user_id}")
    suspend fun getReviewedCVs(
        @Path("user_id") userId: String
    ): Response<List<CVListResponse>>

//    //INI MOCK
//    @GET("/enrollments")
//    suspend fun g2etUserEnrollmentsMK(@Query("user_id") userId: String): Response<List<Enrollment>>
//
//    @GET("api/v1/feature/courses/user/{userId}/enrollments")
//    suspend fun getUserEnrollmentsMK(@Path("userId") userId: String): Response<List<EnrollMock>>
//
//    @GET("api/v1/feature/courses")
//    suspend fun getAllCoursesMK(): Response<List<Course>>

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

    //Roadmap
    @GET("api/v1/roadmaps")
    suspend fun getAllRoadmaps(): Response<List<Roadmap>>

    @GET("api/v1/roadmaps/{rm_id}")
    suspend fun getRoadmapDetails(
        @Path("rm_id") roadmapId: String
    ): Response<Roadmap>

    @POST("api/v1/users/{user_id}/roadmaps/{rm_id}/start")
    suspend fun startRoadmap(
        @Path("user_id") userId: String,
        @Path("rm_id") roadmapId: String
    ): Response<UserRoadmap>

    @GET("api/v1/users/{user_id}/roadmaps")
    suspend fun getUserRoadmaps(
        @Path("user_id") userId: String
    ): Response<List<UserRoadmapHistory>>

    @POST("api/v1/users/{user_id}/roadmaps/{roadmap_id}/steps/{step_id}/complete")
    suspend fun completeStep(
        @Path("user_id") userId: String,
        @Path("roadmap_id") roadmapId: String,
        @Path("step_id") stepId: String
    ): Response<CompletedStepResponse>

    @GET("api/v1/users/{user_id}/roadmaps/{roadmap_id}/progress")
    suspend fun getRoadmapProgress(
        @Path("user_id") userId: String,
        @Path("roadmap_id") roadmapId: String
    ): Response<List<ProgressStep>>

    @GET("api/v1/users/{user_id}/roadmaps/{roadmap_id}")
    suspend fun getUserRoadmapProgress(
        @Path("user_id") userId: String,
        @Path("roadmap_id") roadmapId: String
    ): Response<UserRoadmapHistory>

    @DELETE("api/v1/users/{user_id}/roadmaps/{roadmap_id}")
    suspend fun deleteRoadmap(
        @Path("user_id") userId: String,
        @Path("roadmap_id") roadmapId: String
    ): Response<Unit>

    // -------------------- Mentorship Feature --------------------

    @POST("api/v1/mentorship/register")
    suspend fun registerMentor(
        @Body request : Map<String, String>
    ): Response<MentorProfile>

    @GET("api/v1/mentorship/mentors")
    suspend fun listMentors(
        @Query("q") query: String = ""
    ): Response<List<MentorProfile>>

    @POST("api/v1/mentorship/mentors/{mentor_id}/availability")
    suspend fun setAvailability(
        @Path("mentor_id") mentorId : String,
        @Body request : Map<String, String>
    ): Response<Availability>

    @GET("api/v1/mentorship/mentors/{mentor_id}/availability")
    suspend fun getAvailability(
        @Path("mentor_id") mentorId: String
    ): Response<List<Availability>>

    @POST("api/v1/mentorship/sessions")
    suspend fun bookSession(
        @Body request : Map<String, String>
    ): Response<MentorshipSession>

    @POST("api/v1/mentorship/sessions/{session_id}/feedback")
    suspend fun submitFeedback(
        @Path("session_id") sessionId : String,
        @Body request : Map<String, String>
    ): Response<MentorFeedback>

    @GET("api/v1/mentorship/mentors/{mentor_id}/profile")
    suspend fun getMentorProfile(
        @Path("mentor_id") mentorId: String
    ): Response<MentorProfileWithExperience>


}