package com.mercu.intrain.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.RegisterRequest
import com.mercu.intrain.API.RegisterResponse
import com.mercu.intrain.databinding.ActivityRegisterBinding
import com.mercu.intrain.sharedpref.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loading.visibility = View.GONE
        sharedPrefHelper = SharedPrefHelper (this)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameLayout.text.toString()
            val email = binding.emailLayout.text.toString()
            val name = binding.nameLayout.text.toString()
            val password = binding.passwordLayout.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty()) {
                binding.loading.visibility = View.VISIBLE
                register(username, password,name,email)
            } else {
                showToast("Please enter your data")
            }
        }

        binding.loginLink.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            navigateToLoginActivity()
            binding.loading.visibility = View.GONE
        }



    }

    private fun register (username: String, password: String, name: String,email: String){
        val registerRequest = RegisterRequest(username, password, name, email)
        ApiConfig.api.registerRequest(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse?>,
                response: Response<RegisterResponse?>
            ) {
                binding.loading.visibility = View.GONE
                if (response.isSuccessful) {
                    val RegisterResponse = response.body()
                    if (RegisterResponse != null) {
                        navigateToLoginActivity()
                    } else {
                        showToast(RegisterResponse?.message ?: "Register Failed.")
                    }
                } else {
                    showToast("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                binding.loading.visibility = View.GONE
                showToast("Network request failed: ${t.message}")
            }



        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}