package com.mercu.intrain.ui.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.CVListResponse
import com.mercu.intrain.API.CVReviewHistoryResponse
import com.mercu.intrain.API.ChatSession
import com.mercu.intrain.repository.HistoryRepository
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiConfig.api
    private val repository = HistoryRepository(apiService)

    private val pref = SharedPrefHelper(application)
    private val userId = pref.getUid()

    private val _chatHistory = MutableLiveData<List<ChatSession>>()
    val chatHistory: LiveData<List<ChatSession>> = _chatHistory

    private val _cvReviews = MutableLiveData<List<CVReviewHistoryResponse>>()
    val cvReviews: LiveData<List<CVReviewHistoryResponse>> = _cvReviews

    private val _reviewedCVList = MutableLiveData<List<CVListResponse>>()
    val reviewedCVList: LiveData<List<CVListResponse>> = _reviewedCVList

    fun loadAllHistories() {
        userId?.let { uid ->
            Log.d("HistoryViewModel", "Loading histories for user: $uid")
            viewModelScope.launch {
                try {
                    // Load chat history
                    Log.d("HistoryViewModel", "Loading chat history...")
                    val chatHistoryResponse = repository.getUserChatHistory(uid)
                    Log.d("HistoryViewModel", "Chat history response: ${chatHistoryResponse.chats.size} chats")
                    _chatHistory.value = chatHistoryResponse.chats
                    
                    // Load CV reviews
                    Log.d("HistoryViewModel", "Loading CV reviews...")
                    val cvReviewsList = repository.getCVReviews(uid)
                    Log.d("HistoryViewModel", "CV reviews response: ${cvReviewsList.size} reviews")
                    _cvReviews.value = cvReviewsList
                    
                    // Load reviewed CV list
                    Log.d("HistoryViewModel", "Loading reviewed CV list...")
                    val reviewedCVList = repository.getReviewedCVList(uid)
                    Log.d("HistoryViewModel", "Reviewed CV list response: ${reviewedCVList.size} CVs")
                    _reviewedCVList.value = reviewedCVList
                    
                } catch (e: Exception) {
                    Log.e("HistoryViewModel", "Error loading histories: ${e.message}", e)
                }
            }
        } ?: run {
            Log.e("HistoryViewModel", "User ID is null, cannot load histories")
        }
    }

    // Test function to debug chat history endpoint
    fun testChatHistoryEndpoint() {
        userId?.let { uid ->
            Log.d("HistoryViewModel", "Testing chat history endpoint for user: $uid")
            viewModelScope.launch {
                try {
                    val response = apiService.getChatHistory(uid)
                    Log.d("HistoryViewModel", "Raw response code: ${response.code()}")
                    Log.d("HistoryViewModel", "Raw response message: ${response.message()}")
                    Log.d("HistoryViewModel", "Raw response body: ${response.body()}")
                    Log.d("HistoryViewModel", "Raw response error body: ${response.errorBody()?.string()}")
                } catch (e: Exception) {
                    Log.e("HistoryViewModel", "Test endpoint error: ${e.message}", e)
                }
            }
        }
    }
}
