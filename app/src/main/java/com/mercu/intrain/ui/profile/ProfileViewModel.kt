package com.mercu.intrain.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.model.WorkExperience
import kotlinx.coroutines.launch

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {

    private val _name = MutableLiveData<String>().apply {
        value = "Loading..."
    }
    val name: LiveData<String> = _name

    private val _username = MutableLiveData<String>().apply {
        value = "Loading..."
    }
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>().apply {
        value = "Loading..."
    }
    val email: LiveData<String> = _email

    private val _workExperiences = MutableLiveData<List<WorkExperience>>()
    val workExperiences: LiveData<List<WorkExperience>> = _workExperiences

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun updateProfileInfo(name: String, username: String, email: String) {
        _name.value = name
        _username.value = username
        _email.value = email
    }

    fun loadWorkExperiences(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getWorkExperiences(userId)
                if (response.isSuccessful) {
                    _workExperiences.value = response.body()
                } else {
                    _error.value = "Failed to load work experiences"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createWorkExperience(userId: String, workExperience: WorkExperience) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.createWorkExperience(userId, workExperience)
                if (response.isSuccessful) {
                    loadWorkExperiences(userId) // Reload the list
                } else {
                    _error.value = "Failed to create work experience"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateWorkExperience(userId: String, expId: String, workExperience: WorkExperience) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.updateWorkExperience(userId, expId, workExperience)
                if (response.isSuccessful) {
                    loadWorkExperiences(userId) // Reload the list
                } else {
                    _error.value = "Failed to update work experience"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteWorkExperience(userId: String, expId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deleteWorkExperience(userId, expId)
                if (response.isSuccessful) {
                    loadWorkExperiences(userId) // Reload the list
                } else {
                    _error.value = "Failed to delete work experience"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ProfileViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}