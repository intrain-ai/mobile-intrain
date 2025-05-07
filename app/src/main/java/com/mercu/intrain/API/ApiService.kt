package com.mercu.intrain.API

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
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
    ): Call<ResgiterResponse>

//    @Multipart
//    @POST("upload")
//    suspend fun uploadPdfWithText(
//        @Part pdf: MultipartBody.Part,
//        @Part("description") description: RequestBody
//    ): Response<CVuploadResponse>

    @POST("/api/v1/feature/interview/chat")
    fun chatRequestInit(
        @Body request: ChatRequest
    ): Call<ChatInitResponse>

    @POST("/api/v1/feature/interview/chat")
    fun chatRequestContinous(
        @Body request: ChatContinous
    ): Call<ResponseBody>


}