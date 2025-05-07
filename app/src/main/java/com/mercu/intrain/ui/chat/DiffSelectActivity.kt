package com.mercu.intrain.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ChatInitResponse
import com.mercu.intrain.API.ChatRequest
import com.mercu.intrain.API.LoginResponse
import com.mercu.intrain.R
import com.mercu.intrain.adapter.DiffSelectAdapter
import com.mercu.intrain.databinding.ActivityDiffSelectBinding
import com.mercu.intrain.model.Difficulty
import com.mercu.intrain.model.Message
import com.mercu.intrain.sharedpref.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiffSelectActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: DiffSelectAdapter
    private lateinit var binding: ActivityDiffSelectBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiffSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefHelper = SharedPrefHelper(this)


        val difficultyList = listOf(
            Difficulty(1, "Easy", R.drawable.diff_easy, "AI will be friendly and helpful"),
            Difficulty(2, "Medium", R.drawable.diff_med, "AI will provide mixed messages"),
            Difficulty(3, "Hard", R.drawable.diff_hard, "AI will be hard to please and ask more cryptic message")
        )

        viewPager = findViewById(R.id.viewPager)
        adapter = DiffSelectAdapter(difficultyList)
        viewPager.adapter = adapter

       binding.btnSelect.setOnClickListener {
            val selected = difficultyList[viewPager.currentItem]
           val userId = sharedPrefHelper.getUid().toString()
           val hrLevel = selected.id
           val jobType = "Back-End Engineer"
           initializeChat(userId,hrLevel,jobType)
           Log.d("DIFFICULTY", "Selected ID: ${selected.id}")

        }
    }

    fun initializeChat (userId: String,hrLevel: Int,jobType: String){
        val initChat = ChatRequest(jobType,userId,hrLevel)
        ApiConfig.api.chatRequestInit(initChat).enqueue(object : Callback<ChatInitResponse>{
            override fun onResponse(call: Call<ChatInitResponse?>, response: Response<ChatInitResponse?>) {
                if (response.isSuccessful) {
                    val chatInitResponse = response.body()
                    if (chatInitResponse?.response != null) {
                        val intent = Intent(this@DiffSelectActivity, ChatActivity::class.java)
                        intent.putExtra("chat_response", chatInitResponse.response)
                        intent.putExtra("session_id", chatInitResponse.sessionId)
                        intent.putExtra("hr_level", hrLevel)
                        startActivity(intent)
                    } else {
                        showToast("Error")
                    }
                } else {
                    showToast("Server error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ChatInitResponse>, t: Throwable) {
                showToast("Network request failed: ${t.message}")
            }
        })
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
