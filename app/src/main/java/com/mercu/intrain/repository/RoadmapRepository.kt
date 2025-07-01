package com.mercu.intrain.repository
import android.content.Context
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.model.Achievement
import com.mercu.intrain.model.CompletedStepResponse
import com.mercu.intrain.model.ProgressStep
import com.mercu.intrain.model.Roadmap
import com.mercu.intrain.model.UserRoadmap
import com.mercu.intrain.model.UserRoadmapHistory
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class RoadmapRepository(private val context: Context) {
    private val api = ApiConfig.api
    private val sharedPref = SharedPrefHelper(context)

    private fun getUserId(): String {
        return sharedPref.getUid() ?: throw IllegalStateException("User not authenticated")
    }

    suspend fun getAllRoadmaps(): Response<List<Roadmap>> =
        withContext(Dispatchers.IO) { api.getAllRoadmaps() }

    suspend fun getRoadmapDetails(roadmapId: String): Response<Roadmap> =
        withContext(Dispatchers.IO) { api.getRoadmapDetails(roadmapId) }

    suspend fun startRoadmap(roadmapId: String): Response<UserRoadmap> =
        withContext(Dispatchers.IO) { api.startRoadmap(getUserId(), roadmapId) }

    suspend fun getUserRoadmaps(): Response<List<UserRoadmapHistory>> =
        withContext(Dispatchers.IO) { api.getUserRoadmaps(getUserId()) }

    suspend fun completeStep(roadmapId: String, stepId: String): Response<CompletedStepResponse> =
        withContext(Dispatchers.IO) { api.completeStep(getUserId(), roadmapId, stepId) }

    suspend fun getRoadmapProgress(roadmapId: String): Response<List<ProgressStep>> =
        withContext(Dispatchers.IO) { api.getRoadmapProgress(getUserId(), roadmapId) }

    suspend fun getUserRoadmapProgress(roadmapId: String): Response<UserRoadmapHistory> =
        withContext(Dispatchers.IO) { api.getUserRoadmapProgress(getUserId(), roadmapId) }

    suspend fun deleteRoadmap(roadmapId: String): Response<Unit> =
        withContext(Dispatchers.IO) { api.deleteRoadmap(getUserId(), roadmapId) }

    suspend fun getUserAchievements(userId: String): Response<List<Achievement>> {
        return api.getUserAchievements(userId)
    }
}