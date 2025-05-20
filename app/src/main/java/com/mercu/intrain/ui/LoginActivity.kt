package com.mercu.intrain.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.LoginRequest
import com.mercu.intrain.API.LoginResponse
import com.mercu.intrain.MainActivity
import com.mercu.intrain.R
import com.mercu.intrain.databinding.ActivityLoginBinding
import com.mercu.intrain.model.Message
import com.mercu.intrain.sharedpref.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefHelper = SharedPrefHelper(this)

        supportActionBar?.hide()
        binding.loading.visibility = View.GONE

        enableEdgeToEdge()

        binding.login.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            val usernameBinding = binding.usernameLayout
            val passwordBinding = binding.passwordLayout
            val username = usernameBinding.text.toString()
            val password = passwordBinding.text.toString()
            binding.usernameLayout.requestFocus()
            binding.passwordLayout.requestFocus()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                binding.loading.visibility = View.VISIBLE
                login(username, password)
                binding.loading.visibility = View.GONE
            } else {
                showToast("Please enter email and password")
                binding.loading.visibility = View.GONE
            }
        }



        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun login(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        ApiConfig.api.loginRequest(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                binding.loading.visibility = View.GONE
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        sharedPrefHelper.saveUid(loginResponse.user.id)
                        sharedPrefHelper.saveName(loginResponse.user.name ?: "")
                        sharedPrefHelper.saveUsername(loginResponse.user.username)
                        sharedPrefHelper.saveEmail(loginResponse.user.email)
                        Log.d("VALUE", sharedPrefHelper.getUid().toString())
                        navigateToMainActivity()
                    } else {
                        showToast(loginResponse?.message ?: "Login failed.")
                    }
                } else {
                    showToast("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.loading.visibility = View.GONE
                showToast("Network request failed: ${t.message}")
            }


        })


    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}