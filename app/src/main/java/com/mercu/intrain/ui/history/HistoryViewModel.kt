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
            viewModelScope.launch {
                try {
                    _chatHistory.value = repository.getUserChatHistory(uid).chats
                    _cvReviews.value = repository.getCVReviews(uid)
                    _reviewedCVList.value = repository.getReviewedCVList(uid)
                } catch (e: Exception) {
                    Log.e("HistoryVM", "Error: ${e.message}")
                }
            }
        }
    }
}
