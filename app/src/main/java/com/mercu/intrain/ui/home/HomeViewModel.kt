package com.mercu.intrain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    // LiveData to hold the data for the UI elements
    private val _text = MutableLiveData<String>().apply {
        value = "Hello, Aldi!"
    }
    val text: LiveData<String> = _text

    private val _courseCompleted = MutableLiveData<Int>().apply {
        value = 12 // Sample number of courses completed
    }
    val courseCompleted: LiveData<Int> = _courseCompleted

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

    // Example of updating data in ViewModel
    fun updateActivity(activity: String) {
        _activityContent.value = activity
    }
}
