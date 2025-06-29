package com.mercu.intrain.ui.mentor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MentorViewModel : ViewModel() {

    // State untuk mentor list
    private val _mentorListState = MutableStateFlow<MentorListState>(MentorListState.Loading)
    val mentorListState: StateFlow<MentorListState> = _mentorListState.asStateFlow()

    // State untuk mentor registration
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    // State untuk availability
    private val _availabilityState = MutableStateFlow<AvailabilityState>(AvailabilityState.Idle)
    val availabilityState: StateFlow<AvailabilityState> = _availabilityState.asStateFlow()

    // State untuk list availability
    private val _availabilityListState = MutableStateFlow<AvailabilityListState>(AvailabilityListState.Loading)
    val availabilityListState: StateFlow<AvailabilityListState> = _availabilityListState.asStateFlow()

    // State untuk mentor profile
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    // State untuk session booking
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Idle)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    // NEW: State for feedback submission
    private val _feedbackState = MutableStateFlow<FeedbackState>(FeedbackState.Idle)
    val feedbackState: StateFlow<FeedbackState> = _feedbackState.asStateFlow()

    private val apiService = ApiConfig.api

    init {
        fetchMentors()
    }

    fun fetchMentors(query: String = "") {
        viewModelScope.launch {
            _mentorListState.value = MentorListState.Loading
            try {
                val response = apiService.listMentors(query.replace(" ", "_"))
                if (response.isSuccessful) {
                    val mentors = response.body() ?: emptyList()
                    _mentorListState.value = MentorListState.Success(mentors)
                } else {
                    _mentorListState.value = MentorListState.Error(
                        "Failed to load mentors: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _mentorListState.value = MentorListState.Error(
                    "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun fetchMentorProfile(mentorId: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = apiService.getMentorProfile(mentorId)
                if (response.isSuccessful && response.body() != null) {
                    _profileState.value = ProfileState.Success(response.body()!!)
                } else {
                    _profileState.value = ProfileState.Error(
                        "Failed to load profile: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(
                    "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun submitFeedback(sessionId: String, rating: Int, feedback: String) {
        viewModelScope.launch {
            _feedbackState.value = FeedbackState.Loading
            try {
                // Convert rating to String
                val body = mapOf(
                    "rating" to rating.toString(), // Convert Int to String
                    "feedback" to feedback
                )
                val response = apiService.submitFeedback(sessionId, body)
                if (response.isSuccessful && response.body() != null) {
                    _feedbackState.value = FeedbackState.Success(response.body()!!)
                } else {
                    _feedbackState.value = FeedbackState.Error(
                        "Failed to submit feedback: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _feedbackState.value = FeedbackState.Error(
                    "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun fetchAvailabilities(mentorId: String) {
        viewModelScope.launch {
            _availabilityListState.value = AvailabilityListState.Loading
            try {
                val response = apiService.getAvailability(mentorId)
                if (response.isSuccessful) {
                    val availabilities = response.body() ?: emptyList()
                    _availabilityListState.value = AvailabilityListState.Success(availabilities)
                } else {
                    _availabilityListState.value = AvailabilityListState.Error(
                        "Failed to load availability: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _availabilityListState.value = AvailabilityListState.Error(
                    "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun registerMentor(userId: String, expertise: String, bio: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val body = mapOf(
                    "user_id" to userId,
                    "expertise" to expertise,
                    "bio" to bio
                )
                val response = apiService.registerMentor(body)
                if (response.isSuccessful && response.body() != null) {
                    _registerState.value = RegisterState.Success(response.body()!!)
                    // Refresh mentor list after registration
                    fetchMentors()
                } else {
                    _registerState.value = RegisterState.Error(
                        "Registration failed: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(
                    "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun setAvailability(mentorId: String, start: String, end: String) {
        viewModelScope.launch {
            _availabilityState.value = AvailabilityState.Loading
            try {
                val body = mapOf(
                    "start_datetime" to start,
                    "end_datetime" to end
                )
                val response = apiService.setAvailability(mentorId, body)
                if (response.isSuccessful && response.body() != null) {
                    _availabilityState.value = AvailabilityState.Success(response.body()!!)
                    // Refresh availability list
                    fetchAvailabilities(mentorId)
                } else {
                    _availabilityState.value = AvailabilityState.Error(
                        "Failed to set availability: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _availabilityState.value = AvailabilityState.Error(
                    "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun bookSession(menteeId: String, availabilityId: String) {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            try {
                val body = mapOf(
                    "mentee_id" to menteeId,
                    "availability_id" to availabilityId
                )
                val response = apiService.bookSession(body)
                if (response.isSuccessful && response.body() != null) {
                    _sessionState.value = SessionState.Success(response.body()!!)
                } else {
                    _sessionState.value = SessionState.Error(
                        "Booking failed: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _sessionState.value = SessionState.Error(
                    "Network error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    fun resetAvailabilityState() {
        _availabilityState.value = AvailabilityState.Idle
    }

    fun resetSessionState() {
        _sessionState.value = SessionState.Idle
    }

    fun resetFeedbackState() {
        _feedbackState.value = FeedbackState.Idle
    }

    sealed class FeedbackState {
        object Idle : FeedbackState()
        object Loading : FeedbackState()
        data class Success(val feedback: MentorFeedback) : FeedbackState()
        data class Error(val message: String) : FeedbackState()
    }

    // Sealed classes untuk state management
    sealed class MentorListState {
        object Loading : MentorListState()
        data class Success(val mentors: List<MentorProfile>) : MentorListState()
        data class Error(val message: String) : MentorListState()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val mentor: MentorProfile) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    sealed class AvailabilityState {
        object Idle : AvailabilityState()
        object Loading : AvailabilityState()
        data class Success(val availability: Availability) : AvailabilityState()
        data class Error(val message: String) : AvailabilityState()
    }

    sealed class AvailabilityListState {
        object Loading : AvailabilityListState()
        data class Success(val availabilities: List<Availability>) : AvailabilityListState()
        data class Error(val message: String) : AvailabilityListState()
    }

    sealed class ProfileState {
        object Loading : ProfileState()
        data class Success(val profile: MentorProfileWithExperience) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    sealed class SessionState {
        object Idle : SessionState()
        object Loading : SessionState()
        data class Success(val session: MentorshipSession) : SessionState()
        data class Error(val message: String) : SessionState()
    }
}