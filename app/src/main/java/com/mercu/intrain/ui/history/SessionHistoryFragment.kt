package com.mercu.intrain.ui.history

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ChatMessage
import com.mercu.intrain.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SessionHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var backButton: FloatingActionButton
    private lateinit var adapter: SessionHistoryAdapter
    private var sessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionId = arguments?.getString(ARG_SESSION_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_session_history, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        backButton = view.findViewById(R.id.fabBack)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SessionHistoryAdapter()
        recyclerView.adapter = adapter
        loadSessionHistory()
    }

    private fun loadSessionHistory() {
        val id = sessionId ?: return
        progressBar.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        recyclerView.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiConfig.api.getSessionHistory(id)
                if (response.isSuccessful) {
                    val history = response.body()?.history ?: emptyList()
                    if (history.isEmpty()) {
                        errorText.text = "No chat history found."
                        errorText.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        adapter.submitList(history)
                        recyclerView.visibility = View.VISIBLE
                        errorText.visibility = View.GONE
                    }
                } else {
                    errorText.text = "Failed to load chat history."
                    errorText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                errorText.text = "Error: ${e.message}"
                errorText.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val ARG_SESSION_ID = "session_id"
        fun newInstance(sessionId: String): SessionHistoryFragment {
            val fragment = SessionHistoryFragment()
            val args = Bundle()
            args.putString(ARG_SESSION_ID, sessionId)
            fragment.arguments = args
            return fragment
        }
    }
}

class SessionHistoryAdapter : RecyclerView.Adapter<SessionHistoryAdapter.MessageViewHolder>() {
    private val items = mutableListOf<ChatMessage>()

    fun submitList(data: List<ChatMessage>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        private val bubbleContainer: LinearLayout = itemView.findViewById(R.id.bubbleContainer)
        private val messageContainer: LinearLayout = itemView.findViewById(R.id.messageContainer)
        private val leftSpace: View = itemView.findViewById(R.id.leftSpace)
        private val rightSpace: View = itemView.findViewById(R.id.rightSpace)

        fun bind(data: ChatMessage) {
            val isUser = data.sender == "user"
            val message = data.content.questionText ?: data.content.text
            messageText.text = message ?: "[Empty Message]"
            messageTime.text = formatTime(data.sentAt)

            if (isUser) {
                bubbleContainer.setBackgroundResource(R.drawable.bg_message_user)
                messageText.setTextColor(itemView.context.getColor(android.R.color.white))
                messageContainer.gravity = Gravity.END
                leftSpace.visibility = View.VISIBLE
                rightSpace.visibility = View.GONE
            } else {
                bubbleContainer.setBackgroundResource(R.drawable.bg_message_bot)
                messageText.setTextColor(itemView.context.getColor(android.R.color.black))
                messageContainer.gravity = Gravity.START
                leftSpace.visibility = View.GONE
                rightSpace.visibility = View.VISIBLE
            }
        }

        private fun formatTime(iso: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(iso)
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                date?.let { formatter.format(it) } ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }
}
