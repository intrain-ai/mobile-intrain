package com.mercu.intrain.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _username = MutableLiveData<String>().apply {
        value = "Loading..."
    }
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>().apply {
        value = "Loading..."
    }
    val email: LiveData<String> = _email

    fun updateProfileInfo(username: String, email: String) {
        _username.value = username
        _email.value = email
    }
}