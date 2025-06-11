package com.mercu.intrain.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.Article
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.API.NewsConfig
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sharedPrefHelper: SharedPrefHelper,
    private val apiService: ApiService
) : ViewModel() {

    private val _newsArticles = MutableLiveData<List<Article>>()
    val newsArticles: LiveData<List<Article>> = _newsArticles

    private val _completionPercentage = MutableLiveData<Int>()
    val completionPercentage: LiveData<Int> = _completionPercentage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _name = MutableLiveData<String>().apply {
        value = "Hello, ${sharedPrefHelper.getUsername() ?: "User"}!"
    }
    val name: LiveData<String> = _name

    private val _courseDescription = MutableLiveData<String>().apply {
        value = "Course Completion"
    }
    val courseDescription: LiveData<String> = _courseDescription

    private val _activityContent = MutableLiveData<String>().apply {
        value = "Let's check your activity today"
    }
    val activityContent: LiveData<String> = _activityContent

    init {
        loadNews()
        loadCourseData()
    }

    fun loadCourseData() {
        val userId = sharedPrefHelper.getUid()
        if (userId == null) {
            _completionPercentage.postValue(0)
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.getUserEnrollments(userId)
                if (response.isSuccessful) {
                    val enrollments = response.body() ?: emptyList()
                    val totalEnrolled = enrollments.size
                    if (totalEnrolled == 0) {
                        _completionPercentage.postValue(0)
                    } else {
                        val completedCount = enrollments.count { it.isCompleted == true }
                        // Kalkulasi persentase (pastikan menggunakan float untuk pembagian)
                        val percentage = (completedCount.toFloat() / totalEnrolled.toFloat()) * 100
                        _completionPercentage.postValue(percentage.toInt())
                    }
                    Log.d("HomeViewModel", "Course data loaded successfully")
                } else {
                    _completionPercentage.postValue(0)
                    Log.e("HomeViewModel", "Failed to load course enrollments")
                }
            } catch (e: Exception) {
                _completionPercentage.postValue(0)
                Log.e("HomeViewModel", "Error loading course data: ${e.message}")
            }
        }
    }

    fun loadNews() {
        viewModelScope.launch {
            try {
                val response = NewsConfig.api.getNews(
                    query = "jobseeker",
                    apiKey = "bf4374ec295e42a99952261bef02bbb9",
                    language = "en",
                    pageSize = 5
                )
                if (response.isSuccessful) {
                    _newsArticles.postValue(response.body()?.articles ?: emptyList())
                } else {
                    _errorMessage.postValue("Gagal memuat berita: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}