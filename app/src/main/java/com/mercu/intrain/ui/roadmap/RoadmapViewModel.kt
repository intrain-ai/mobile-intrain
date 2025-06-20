package com.mercu.intrain.ui.roadmap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.model.ProgressStep
import com.mercu.intrain.model.Roadmap
import com.mercu.intrain.model.UserRoadmapHistory
import com.mercu.intrain.repository.RoadmapRepository
import kotlinx.coroutines.launch

class RoadmapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoadmapRepository(application)

    private val _roadmaps = MutableLiveData<List<Roadmap>>()
    val roadmaps: LiveData<List<Roadmap>> = _roadmaps

    private val _roadmapDetails = MutableLiveData<Roadmap>()
    val roadmapDetails: LiveData<Roadmap> = _roadmapDetails

    private val _userRoadmaps = MutableLiveData<List<UserRoadmapHistory>>()
    val userRoadmaps: LiveData<List<UserRoadmapHistory>> = _userRoadmaps

    private val _roadmapProgress = MutableLiveData<List<ProgressStep>>()
    val roadmapProgress: LiveData<List<ProgressStep>> = _roadmapProgress

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadAllRoadmaps() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAllRoadmaps()
                if (response.isSuccessful) {
                    _roadmaps.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRoadmapDetails(roadmapId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getRoadmapDetails(roadmapId)
                if (response.isSuccessful) {
                    _roadmapDetails.value = response.body()
                } else {
                    _errorMessage.value = "Error loading roadmap: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startRoadmap(roadmapId: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.startRoadmap(roadmapId)
                if (response.isSuccessful) {
                    loadUserRoadmaps()
                    onSuccess()
                } else {
                    _errorMessage.value = "Failed to start roadmap: ${response.code()}"
                }
            } catch (e: IllegalStateException) {
                _errorMessage.value = "Authentication required"
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserRoadmaps() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                android.util.Log.d("RoadmapViewModel", "Loading user roadmaps...")
                val response = repository.getUserRoadmaps()
                android.util.Log.d("RoadmapViewModel", "Response code: ${response.code()}")
                
                when {
                    response.isSuccessful -> {
                        val roadmaps = response.body()
                        android.util.Log.d("RoadmapViewModel", "Received roadmaps: ${roadmaps?.size ?: 0}")
                        if (roadmaps != null) {
                            _userRoadmaps.value = roadmaps
                        } else {
                            android.util.Log.e("RoadmapViewModel", "Response body is null")
                            _errorMessage.value = "No roadmaps data received"
                        }
                    }
                    response.code() == 401 -> {
                        android.util.Log.e("RoadmapViewModel", "Authentication required")
                        _errorMessage.value = "Please login to view your roadmaps"
                    }
                    response.code() == 403 -> {
                        android.util.Log.e("RoadmapViewModel", "Permission denied")
                        _errorMessage.value = "You don't have permission to view roadmaps"
                    }
                    else -> {
                        android.util.Log.e("RoadmapViewModel", "Error: ${response.code()} - ${response.message()}")
                        _errorMessage.value = "Error loading your roadmaps: ${response.code()} - ${response.message()}"
                    }
                }
            } catch (e: IllegalStateException) {
                android.util.Log.e("RoadmapViewModel", "Authentication error", e)
                _errorMessage.value = "Authentication required. Please login first."
            } catch (e: Exception) {
                android.util.Log.e("RoadmapViewModel", "Network error", e)
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeStep(roadmapId: String, stepId: String) {
        viewModelScope.launch {
            try {
                val response = repository.completeStep(roadmapId, stepId)
                if (response.isSuccessful) {
                    loadRoadmapProgress(roadmapId)
                } else {
                    _errorMessage.value = "Failed to complete step: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun loadRoadmapProgress(roadmapId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getRoadmapProgress(roadmapId)
                if (response.isSuccessful) {
                    _roadmapProgress.value = response.body()
                } else {
                    _errorMessage.value = "Error loading progress: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRoadmap(roadmapId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.deleteRoadmap(roadmapId)
                if (response.isSuccessful) {
                    loadUserRoadmaps()
                } else {
                    _errorMessage.value = "Failed to delete roadmap: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}