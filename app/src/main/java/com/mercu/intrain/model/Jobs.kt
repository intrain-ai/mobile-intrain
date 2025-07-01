package com.mercu.intrain.model

import com.google.gson.annotations.SerializedName

data class JobResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("company") val company: String,
    @SerializedName("description") val description: String,
    @SerializedName("location") val location: String,
    @SerializedName("posted_at") val posted_at: String,
    @SerializedName("requirements") val requirements: String
)