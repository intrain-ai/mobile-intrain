package com.mercu.intrain.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.Article
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.API.NewsConfig
import com.mercu.intrain.model.ProgressStep
import com.mercu.intrain.model.Roadmap
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val _progressPercentage = MutableLiveData<Int>()
    val progressPercentage: LiveData<Int> = _progressPercentage

    private val _roadmapProgress = MutableStateFlow<List<ProgressStep>?>(null)
    val roadmapProgress: StateFlow<List<ProgressStep>?> = _roadmapProgress.asStateFlow()


    private val _selectedRoadmap = MutableStateFlow<Roadmap?>(null)
    val selectedRoadmap: StateFlow<Roadmap?> = _selectedRoadmap.asStateFlow()



    private val _name = MutableLiveData<String>().apply {
        value = "Hello, ${sharedPrefHelper.getUsername() ?: "User"}!"
    }
    val name: LiveData<String> = _name

    private val _courseDescription = MutableLiveData<String>().apply {
        value = "Course Completion"
    }
    val courseDescription: LiveData<String> = _courseDescription


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

    fun getLatestRoadmapId(onResult: (String?) -> Unit) {
        val userId = sharedPrefHelper.getUid()
        if (userId.isNullOrEmpty()) {
            onResult(null)
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.getUserRoadmaps(userId)
                if (response.isSuccessful) {
                    val histories = response.body().orEmpty()
                    if (histories.isNotEmpty()) {
                        val latest = histories.maxByOrNull { it.startedAt }
                        onResult(latest?.roadmapId)
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun getRoadmapDetail(roadmapId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRoadmapDetails(roadmapId)
                if (response.isSuccessful) {
                    _selectedRoadmap.value = response.body()
                } else {
                    _selectedRoadmap.value = null
                }
            } catch (e: Exception) {
                _selectedRoadmap.value = null
            }
        }
    }

    fun getRoadmapProgress() {
        val userId = sharedPrefHelper.getUid()
        if (userId.isNullOrEmpty()) {
            _roadmapProgress.value = emptyList()
            return
        }
        getLatestRoadmapId { roadmapId ->
            if (roadmapId.isNullOrEmpty()) {
                _roadmapProgress.value = emptyList()
                return@getLatestRoadmapId
            }

            viewModelScope.launch {
                try {
                    val response = apiService.getRoadmapProgress(userId, roadmapId)
                    if (response.isSuccessful) {
                        val progressList = response.body() ?: emptyList()
                        Log.d("value", "${progressList.count()}")
                        val completedCount = progressList.count { it.completed }
                        Log.d("value", "$completedCount")
                        val percentage = if (progressList.isNotEmpty()) (completedCount * 100 / progressList.size) else 0
                        _progressPercentage.postValue(percentage)
                    } else {
                        _roadmapProgress.value = emptyList()
                    }
                } catch (e: Exception) {
                    _roadmapProgress.value = emptyList()
                }
            }
        }
    }

}