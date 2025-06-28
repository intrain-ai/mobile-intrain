package com.mercu.intrain.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.LoginRequest
import com.mercu.intrain.API.LoginResponse
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun login(username: String, password: String, sharedPrefHelper: SharedPrefHelper) {
        _isLoading.value = true
        _loginState.value = LoginState.Loading

        val loginRequest = LoginRequest(username, password)
        ApiConfig.api.loginRequest(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        sharedPrefHelper.saveUid(loginResponse.user.id)
                        sharedPrefHelper.saveName(loginResponse.user.name ?: "")
                        sharedPrefHelper.saveUsername(loginResponse.user.username)
                        sharedPrefHelper.saveEmail(loginResponse.user.email)
                        sharedPrefHelper.saveIsMentor(loginResponse.user.isMentor)
                        _loginState.value = LoginState.Success(loginResponse.message)
                    } else {
                        _loginState.value = LoginState.Error("Invalid response from server")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JSONObject(errorBody)
                        jsonObject.getString("error")
                    } catch (e: Exception) {
                        "Login failed: ${response.code()}"
                    }
                    _loginState.value = LoginState.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _loginState.value = LoginState.Error("Network request failed: ${t.message ?: "Unknown error"}")
            }
        })
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}