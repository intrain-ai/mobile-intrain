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
    val message: String? = null,

    @field:SerializedName("user")
    val user: User? = null
) : Parcelable

@Parcelize
data class RegisterRequest(

    @field:SerializedName("password")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("email")
    val createdAt: String? = null,

    ) : Parcelable