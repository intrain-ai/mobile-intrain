package com.mercu.intrain.API

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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


}