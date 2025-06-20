package com.mercu.intrain.model

import com.google.gson.annotations.SerializedName

data class Roadmap(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("job_type") val jobType: String,
    @SerializedName("steps") val steps: List<Step>
)

data class Step(
    @SerializedName("id") val id: String,
    @SerializedName("step_order") val stepOrder: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("course_id") val courseId: String?
)

data class UserRoadmap(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("roadmap_id") val roadmapId: String,
    @SerializedName("started_at") val startedAt: String
)

data class ProgressStep(
    @SerializedName("id") val id: String,
    @SerializedName("step_order") val stepOrder: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("course_id") val courseId: String?,
    @SerializedName("completed") val completed: Boolean,
    @SerializedName("completed_at") val completedAt: String?
)

data class CompletedStepResponse(
    @SerializedName("step_id") val stepId: String,
    @SerializedName("completed_at") val completedAt: String
)

data class UserRoadmapHistory(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("roadmap_id") val roadmapId: String,
    @SerializedName("started_at") val startedAt: String,
    @SerializedName("roadmap") val roadmap: Roadmap
)