package com.mercu.intrain.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.R
import com.mercu.intrain.model.Message

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var adapter: ChatAdapter

    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat) // <- nanti kalau mau lebih rapi bisa ganti nama layout-nya ke `activity_chat`

        recyclerView = findViewById(R.id.recyclerView)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonSend.setOnClickListener {
            val text = editTextMessage.text.toString()
            if (text.isNotBlank()) {
                messages.add(Message(text, true))
                messages.add(Message("Balasan bot untuk: \"$text\"", false))
                adapter.notifyItemRangeInserted(messages.size - 2, 2)
//                updateRecyclerViewVisibility()
                recyclerView.scrollToPosition(messages.size - 1)
                editTextMessage.text.clear()
            }
        }
//        updateRecyclerViewVisibility()
    }

//    private fun updateRecyclerViewVisibility() {
//        recyclerView.visibility = if (messages.isEmpty()) View.INVISIBLE else View.VISIBLE
//    }
}
