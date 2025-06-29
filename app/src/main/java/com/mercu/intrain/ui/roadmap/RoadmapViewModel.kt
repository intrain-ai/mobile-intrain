package com.mercu.intrain.ui.roadmap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.model.ProgressStep
import com.mercu.intrain.model.Roadmap
import com.mercu.intrain.model.UserRoadmapHistory
import com.mercu.intrain.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoadmapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoadmapRepository(application)

    private val _roadmaps = MutableStateFlow<List<Roadmap>>(emptyList())
    val roadmaps: StateFlow<List<Roadmap>> = _roadmaps.asStateFlow()

    private val _roadmapDetails = MutableStateFlow<Roadmap?>(null)
    val roadmapDetails: StateFlow<Roadmap?> = _roadmapDetails.asStateFlow()

    private val _userRoadmaps = MutableStateFlow<List<UserRoadmapHistory>>(emptyList())
    val userRoadmaps: StateFlow<List<UserRoadmapHistory>> = _userRoadmaps.asStateFlow()

    private val _userRoadmapProgress = MutableStateFlow<UserRoadmapHistory?>(null)
    val userRoadmapProgress: StateFlow<UserRoadmapHistory?> = _userRoadmapProgress.asStateFlow()

    private val _roadmapProgress = MutableStateFlow<List<ProgressStep>>(emptyList())
    val roadmapProgress: StateFlow<List<ProgressStep>> = _roadmapProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadAllRoadmaps() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getAllRoadmaps()
                if (response.isSuccessful) {
                    val roadmaps = response.body()
                    _roadmaps.value = roadmaps ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.code()} - ${response.message()}"
                    _roadmaps.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                _roadmaps.value = emptyList()
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
                    val roadmap = response.body()
                    if (roadmap != null) {
                        _roadmapDetails.value = roadmap
                    } else {
                        _errorMessage.value = "No roadmap details received"
                    }
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
                            _userRoadmaps.value = emptyList()
                        }
                    }
                    response.code() == 401 -> {
                        android.util.Log.e("RoadmapViewModel", "Authentication required")
                        _errorMessage.value = "Please login to view your roadmaps"
                        _userRoadmaps.value = emptyList()
                    }
                    response.code() == 403 -> {
                        android.util.Log.e("RoadmapViewModel", "Permission denied")
                        _errorMessage.value = "You don't have permission to view roadmaps"
                        _userRoadmaps.value = emptyList()
                    }
                    else -> {
                        android.util.Log.e("RoadmapViewModel", "Error: ${response.code()} - ${response.message()}")
                        _errorMessage.value = "Error loading your roadmaps: ${response.code()} - ${response.message()}"
                        _userRoadmaps.value = emptyList()
                    }
                }
            } catch (e: IllegalStateException) {
                android.util.Log.e("RoadmapViewModel", "Authentication error", e)
                _errorMessage.value = "Authentication required. Please login first."
                _userRoadmaps.value = emptyList()
            } catch (e: Exception) {
                android.util.Log.e("RoadmapViewModel", "Network error", e)
                _errorMessage.value = "Network error: ${e.message}"
                _userRoadmaps.value = emptyList()
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
                    val progress = response.body()
                    _roadmapProgress.value = progress ?: emptyList()
                } else {
                    _errorMessage.value = "Error loading progress: ${response.code()}"
                    _roadmapProgress.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _roadmapProgress.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserRoadmapProgress(roadmapId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Try to get user roadmap info from the getUserRoadmaps endpoint
                val userRoadmapsResponse = repository.getUserRoadmaps()
                if (userRoadmapsResponse.isSuccessful) {
                    val userRoadmaps = userRoadmapsResponse.body()
                    val userRoadmap = userRoadmaps?.find { it.roadmapId == roadmapId }
                    if (userRoadmap != null) {
                        _userRoadmapProgress.value = userRoadmap
                    } else {
                        // If not found in user roadmaps, try the specific endpoint as fallback
                        val response = repository.getUserRoadmapProgress(roadmapId)
                        if (response.isSuccessful) {
                            val userProgress = response.body()
                            _userRoadmapProgress.value = userProgress
                        } else {
                            // Handle 405 error gracefully - this endpoint might not be implemented
                            if (response.code() == 405) {
                                android.util.Log.w("RoadmapViewModel", "getUserRoadmapProgress endpoint not available (405)")
                                _userRoadmapProgress.value = null
                            } else {
                                _errorMessage.value = "Error loading user progress: ${response.code()}"
                                _userRoadmapProgress.value = null
                            }
                        }
                    }
                } else {
                    // If getUserRoadmaps fails, try the specific endpoint as fallback
                    val response = repository.getUserRoadmapProgress(roadmapId)
                    if (response.isSuccessful) {
                        val userProgress = response.body()
                        _userRoadmapProgress.value = userProgress
                    } else {
                        // Handle 405 error gracefully - this endpoint might not be implemented
                        if (response.code() == 405) {
                            android.util.Log.w("RoadmapViewModel", "getUserRoadmapProgress endpoint not available (405)")
                            _userRoadmapProgress.value = null
                        } else {
                            _errorMessage.value = "Error loading user progress: ${response.code()}"
                            _userRoadmapProgress.value = null
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RoadmapViewModel", "Error loading user roadmap progress", e)
                _userRoadmapProgress.value = null
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