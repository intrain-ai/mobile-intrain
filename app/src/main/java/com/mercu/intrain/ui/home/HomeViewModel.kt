package com.mercu.intrain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.API.Article
import com.mercu.intrain.API.NewsConfig
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _newsArticles = MutableLiveData<List<Article>>()
    val newsArticles: LiveData<List<Article>> = _newsArticles

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // LiveData for course progress (represented as a percentage)
    private val _courseProgress = MutableLiveData<Float>().apply {
        value = 0f
    }
    val courseProgress: LiveData<Float> = _courseProgress

    fun updateProgress(progress: Float) {
        _courseProgress.value = progress
    }

    private val _text = MutableLiveData<String>().apply {
        value = "Hello, Aldi!"
    }
    val text: LiveData<String> = _text

//    private val _courseCompleted = MutableLiveData<Int>().apply {
//        value = 12
//    }
//    val courseCompleted: LiveData<Int> = _courseCompleted

    private val _courseDescription = MutableLiveData<String>().apply {
        value = "Course Completed"
    }
    val courseDescription: LiveData<String> = _courseDescription

    private val _activityContent = MutableLiveData<String>().apply {
        value = "Check out today's activity"
    }
    val activityContent: LiveData<String> = _activityContent

    fun loadNews() {
        viewModelScope.launch {
            try {
                val response = NewsConfig.api.getNews(
                    query = "pekerjaan",
                    apiKey = "bf4374ec295e42a99952261bef02bbb9",
                    language = "id",
                    pageSize = 5
                )

                if (response.isSuccessful) {
                    _newsArticles.postValue(response.body()?.articles ?: emptyList())
                } else {
                    _errorMessage.postValue("Failed to load news: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Network error: ${e.message}")
            }
        }
    }
}
