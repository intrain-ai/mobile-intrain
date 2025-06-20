package com.mercu.intrain.ui.history

import com.mercu.intrain.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.API.CVReviewHistoryResponse
import com.mercu.intrain.API.ChatSession
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.chip.Chip

class HistoryAdapter(
    private val onChatClick: ((ChatSession) -> Unit)? = null,
    private val onCVClick: ((CVReviewHistoryResponse) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    fun submitList(data: List<Any>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ChatSession -> 0
            is CVReviewHistoryResponse -> 1
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> ChatViewHolder(inflater.inflate(R.layout.item_chat_history, parent, false), onChatClick)
            1 -> CvViewHolder(inflater.inflate(R.layout.item_cv_history, parent, false), onCVClick)
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ChatSession -> (holder as ChatViewHolder).bind(item)
            is CVReviewHistoryResponse -> (holder as CvViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ChatViewHolder(view: View, private val onClick: ((ChatSession) -> Unit)?) : RecyclerView.ViewHolder(view) {
        private val jobText: TextView = view.findViewById(R.id.jobTypeText)
        private val messageText: TextView = view.findViewById(R.id.lastMessageText)
        private val lastMessageTime: TextView = view.findViewById(R.id.lastMessageTime)
        private val chatIcon: ImageView = view.findViewById(R.id.chatIcon)
        private var currentData: ChatSession? = null
        init {
            view.setOnClickListener {
                currentData?.let { onClick?.invoke(it) }
            }
        }
        fun bind(data: ChatSession) {
            currentData = data
            jobText.text = data.jobType
            messageText.text = data.lastMessage
            lastMessageTime.text = formatTime(data.lastMessageAt)
            chatIcon.setImageResource(R.drawable.ic_chat)
        }
        private fun formatTime(iso: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = parser.parse(iso)
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                date?.let { formatter.format(it) } ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }

    class CvViewHolder(view: View, private val onClick: ((CVReviewHistoryResponse) -> Unit)?) : RecyclerView.ViewHolder(view) {
        private val fileName: TextView = view.findViewById(R.id.cvFileNameText)
        private val feedback: TextView = view.findViewById(R.id.feedbackText)
        private val uploadedAt: TextView = view.findViewById(R.id.uploadedAtText)
        private val fileTypeChip: Chip = view.findViewById(R.id.fileTypeChip)
        private val fileIcon: ImageView = view.findViewById(R.id.cvFileIcon)
        private var currentData: CVReviewHistoryResponse? = null
        init {
            view.setOnClickListener {
                currentData?.let { onClick?.invoke(it) }
            }
        }
        fun bind(data: CVReviewHistoryResponse) {
            currentData = data
            fileName.text = data.submission.fileName
            val feedbackText = data.review?.overallFeedback
            if (feedbackText.isNullOrEmpty()) {
                feedback.text = "No feedback yet."
            } else {
                feedback.text = feedbackText
            }
            uploadedAt.text = "Uploaded: ${formatDate(data.submission.uploadedAt)}"
            fileTypeChip.text = data.submission.fileType.uppercase(Locale.getDefault())
            if (data.submission.fileType.equals("pdf", ignoreCase = true)) {
                fileIcon.setImageResource(R.drawable.ic_pdf_placeholder)
            } else {
                fileIcon.setImageResource(R.drawable.ic_file)
            }
        }
        private fun formatDate(iso: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = parser.parse(iso)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                date?.let { formatter.format(it) } ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }

}
