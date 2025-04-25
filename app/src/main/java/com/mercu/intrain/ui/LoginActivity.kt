package com.mercu.intrain.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mercu.intrain.MainActivity
import com.mercu.intrain.R
import com.mercu.intrain.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding.loading.visibility = View.GONE

        enableEdgeToEdge()

        binding.login.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            binding.login.isEnabled = false
            binding.login.text = ""

            Handler(Looper.getMainLooper()).postDelayed({
                binding.loading.visibility = View.GONE
                binding.login.isEnabled = true
                binding.login.text = getString(R.string.login)
            }, 1000)
            }

        binding.register.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}