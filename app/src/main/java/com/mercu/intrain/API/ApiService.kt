package com.mercu.intrain.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("/api/v1/auth/user/login")
    fun loginRequest(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/api/v1/auth/user/register")
    fun registerRequest(
        @Body response: RegisterRequest
    ): Call<ResgiterResponse>


}