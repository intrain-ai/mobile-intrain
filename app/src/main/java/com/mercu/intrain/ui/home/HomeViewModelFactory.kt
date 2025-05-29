package com.mercu.intrain.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.sharedpref.SharedPrefHelper

class HomeViewModelFactory(
    private val sharedPrefHelper: SharedPrefHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(sharedPrefHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
