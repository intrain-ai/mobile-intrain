package com.mercu.intrain.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.RegisterRequest
import com.mercu.intrain.API.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun register(name: String, username: String, email: String, password: String) {
        _isLoading.value = true
        _registerState.value = RegisterState.Loading

        val registerRequest = RegisterRequest(username, password, name, email)
        ApiConfig.api.registerRequest(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        _registerState.value = RegisterState.Success(registerResponse.message.toString())
                    } else {
                        _registerState.value = RegisterState.Error("Registration failed: Invalid response")
                    }
                } else {
                    // Parse error message from response
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JSONObject(errorBody)
                        jsonObject.getString("error")
                    } catch (e: Exception) {
                        "Registration failed: ${response.code()}"
                    }
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _registerState.value = RegisterState.Error("Network request failed: ${t.message ?: "Unknown error"}")
            }
        })
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}