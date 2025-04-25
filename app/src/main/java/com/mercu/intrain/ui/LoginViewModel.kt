package com.mercu.intrain.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()

    fun login() {
        viewModelScope.launch {
            _isLogin.value = true

        }
    }
}
