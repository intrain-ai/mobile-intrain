package com.mercu.intrain.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercu.intrain.MainActivity
import com.mercu.intrain.R
import com.mercu.intrain.databinding.ActivityLoginBinding
import com.mercu.intrain.sharedpref.SharedPrefHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedPrefHelper: SharedPrefHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loading.visibility = View.GONE

        sharedPrefHelper = SharedPrefHelper(this)

        supportActionBar?.hide()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.login.setOnClickListener {
            viewModel.login()
            if (viewModel.isLogin.value == true){
                sharedPrefHelper.isLogin(true)
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}