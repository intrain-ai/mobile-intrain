package com.mercu.intrain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

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

    private val _newsContent = MutableLiveData<String>().apply {
        value = "Latest updates coming soon..."
    }
    val newsContent: LiveData<String> = _newsContent
}
