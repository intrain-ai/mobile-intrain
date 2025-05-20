package com.mercu.intrain.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SharedPrefHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Save data methods
    fun saveUid(uid: String) {
        sharedPreferences.edit().putString("auth_uid", uid).apply()
    }

    fun saveName(name: String) {
        sharedPreferences.edit().putString("name", name).apply()
    }

    fun savePassword(password: String) {
        sharedPreferences.edit().putString("password", password).apply()
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun saveEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun saveDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", isDarkMode).apply()
    }

    fun getUid(): String? {
        return getString("auth_uid")
    }

    fun getUsername(): String? {
        return getString("username")
    }

    fun getName(): String? {
        return getString("name")
    }

    fun getEmail(): String? {
        return getString("email")
    }

    fun getPassword(): String? {
        return getString("password")
    }

    fun getDarkMode(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", false)
    }

    private fun getString(key: String, default: String? = null): String? {
        return sharedPreferences.getString(key, default)
    }

    fun isLoggedIn(): Boolean {
        return getUid() != null
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}
