package com.mercu.intrain.model

import com.google.gson.annotations.SerializedName

data class WorkExperience(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("job_title")
    val jobTitle: String,
    
    @SerializedName("company_name")
    val companyName: String,
    
    @SerializedName("job_desc")
    val jobDesc: String,
    
    @SerializedName("start_month")
    val startMonth: Int,
    
    @SerializedName("start_year")
    val startYear: Int,
    
    @SerializedName("end_month")
    val endMonth: Int,
    
    @SerializedName("end_year")
    val endYear: Int,
    
    @SerializedName("is_current")
    val isCurrent: Boolean,
    
    @SerializedName("created_at")
    val createdAt: String? = null
) 