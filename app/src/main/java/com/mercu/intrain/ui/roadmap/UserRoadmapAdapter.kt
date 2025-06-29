//package com.mercu.intrain.ui.roadmap
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.mercu.intrain.databinding.ItemUserRoadmapBinding
//import com.mercu.intrain.model.UserRoadmapHistory
//import java.text.SimpleDateFormat
//import java.util.*
//
//class UserRoadmapAdapter(
//    private val onItemClick: (UserRoadmapHistory) -> Unit
//) : ListAdapter<UserRoadmapHistory, UserRoadmapAdapter.ViewHolder>(UserRoadmapDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemUserRoadmapBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    inner class ViewHolder(
//        private val binding: ItemUserRoadmapBinding
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            binding.root.setOnClickListener {
//                val position = bindingAdapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    onItemClick(getItem(position))
//                }
//            }
//        }
//
//        fun bind(userRoadmap: UserRoadmapHistory) {
//            // Safe access to roadmap properties
//            val roadmap = userRoadmap.roadmap
//            binding.tvTitle.text = roadmap?.title ?: "Unknown Roadmap"
//            binding.tvDescription.text = roadmap?.description ?: "No description available"
//            binding.tvJobType.text = roadmap?.jobType ?: "Unknown Type"
//
//            // Display total steps (progress will be loaded separately)
//            val totalSteps = roadmap?.steps?.size ?: 0
//            binding.tvProgress.text = "$totalSteps steps total"
//            binding.progressIndicator.progress = 0 // Will be updated when progress is loaded
//
//            // Format the date with null safety
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//            val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//            try {
//                val startedAt = userRoadmap.startedAt
//                if (!startedAt.isNullOrEmpty()) {
//                    val date = dateFormat.parse(startedAt)
//                    binding.tvStartedAt.text = "Started: ${displayFormat.format(date)}"
//                } else {
//                    binding.tvStartedAt.text = "Started: Unknown date"
//                }
//            } catch (e: Exception) {
//                binding.tvStartedAt.text = "Started: ${userRoadmap.startedAt ?: "Unknown date"}"
//            }
//        }
//    }
//
//    private class UserRoadmapDiffCallback : DiffUtil.ItemCallback<UserRoadmapHistory>() {
//        override fun areItemsTheSame(oldItem: UserRoadmapHistory, newItem: UserRoadmapHistory): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: UserRoadmapHistory, newItem: UserRoadmapHistory): Boolean {
//            return oldItem == newItem
//        }
//    }
//}