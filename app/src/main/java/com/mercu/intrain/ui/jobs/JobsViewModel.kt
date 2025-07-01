package com.mercu.intrain.ui.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.model.JobResponse
import retrofit2.Response
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class JobViewModel : ViewModel() {

    private val _jobList = MutableStateFlow<List<JobResponse>>(emptyList())
    val jobList: StateFlow<List<JobResponse>> = _jobList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchJobs()
    }

    private fun fetchJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response: Response<List<JobResponse>> = ApiConfig.api.getJobs()
                if (response.isSuccessful) {
                    _jobList.value = response.body() ?: emptyList()
                } else {
                    // log error or handle error state
                    _jobList.value = emptyList()
                }
            } catch (e: Exception) {
                // log error
                e.printStackTrace()
                _jobList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
