package com.mercu.intrain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.Article
import com.mercu.intrain.API.NewsConfig
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sharedPrefHelper: SharedPrefHelper
) : ViewModel() {

    private val _newsArticles = MutableLiveData<List<Article>>()
    val newsArticles: LiveData<List<Article>> = _newsArticles

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _courseProgress = MutableLiveData<Float>().apply {
        value = 0f
    }
    val courseProgress: LiveData<Float> = _courseProgress

    private val _name = MutableLiveData<String>().apply {
        value = "Halo, ${sharedPrefHelper.getUsername() ?: "Pengguna"}"
    }
    val name: LiveData<String> = _name

    private val _courseDescription = MutableLiveData<String>().apply {
        value = "Course Completed"
    }
    val courseDescription: LiveData<String> = _courseDescription

    private val _activityContent = MutableLiveData<String>().apply {
        value = "Check out today's activity"
    }
    val activityContent: LiveData<String> = _activityContent

    fun updateProgress(progress: Float) {
        _courseProgress.value = progress
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
