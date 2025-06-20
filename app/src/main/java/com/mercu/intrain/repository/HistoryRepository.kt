package com.mercu.intrain.repository

import com.mercu.intrain.API.ApiService
import com.mercu.intrain.API.CVListResponse
import com.mercu.intrain.API.CVReviewHistoryResponse
import com.mercu.intrain.API.ChatHistoryResponse

class HistoryRepository(private val apiService: ApiService) {

    suspend fun getUserChatHistory(userId: String): ChatHistoryResponse {
        return apiService.getChatHistory(userId).body() ?: ChatHistoryResponse(emptyList(), userId)
    }

    suspend fun getCVReviews(userId: String): List<CVReviewHistoryResponse> {
        return apiService.getCVReviews(userId).body() ?: emptyList()
    }

    suspend fun getReviewedCVList(userId: String): List<CVListResponse> {
        return apiService.getReviewedCVs(userId).body() ?: emptyList()
    }
}
