package com.mercu.intrain.repository

import android.util.Log
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.API.CVListResponse
import com.mercu.intrain.API.CVReviewHistoryResponse
import com.mercu.intrain.API.ChatHistoryResponse

class HistoryRepository(private val apiService: ApiService) {

    suspend fun getUserChatHistory(userId: String): ChatHistoryResponse {
        return try {
            val response = apiService.getChatHistory(userId)
            if (response.isSuccessful) {
                response.body() ?: ChatHistoryResponse(emptyList(), userId)
            } else {
                Log.e("HistoryRepository", "Chat history API error: ${response.code()} - ${response.message()}")
                ChatHistoryResponse(emptyList(), userId)
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Chat history network error: ${e.message}")
            ChatHistoryResponse(emptyList(), userId)
        }
    }

    suspend fun getCVReviews(userId: String): List<CVReviewHistoryResponse> {
        return try {
            val response = apiService.getCVReviews(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("HistoryRepository", "CV reviews API error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "CV reviews network error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getReviewedCVList(userId: String): List<CVListResponse> {
        return try {
            val response = apiService.getReviewedCVs(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("HistoryRepository", "Reviewed CV list API error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Reviewed CV list network error: ${e.message}")
            emptyList()
        }
    }
}
