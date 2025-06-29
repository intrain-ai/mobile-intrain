package com.mercu.intrain.ui.roadmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.databinding.ItemRoadmapBinding
import com.mercu.intrain.model.Roadmap

class RoadmapAdapter(
    private val onItemClick: (Roadmap) -> Unit
) : ListAdapter<Roadmap, RoadmapAdapter.ViewHolder>(RoadmapDiffCallback()) {

    inner class ViewHolder(val binding: ItemRoadmapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(roadmap: Roadmap) {
            binding.apply {
                tvTitle.text = roadmap.title ?: "Unknown Roadmap"
                tvDescription.text = roadmap.description ?: "No description available"
                tvJobType.text = roadmap.jobType ?: "Unknown Type"

                root.setOnClickListener { onItemClick(roadmap) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoadmapBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RoadmapDiffCallback : DiffUtil.ItemCallback<Roadmap>() {
    override fun areItemsTheSame(oldItem: Roadmap, newItem: Roadmap) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Roadmap, newItem: Roadmap) =
        oldItem == newItem
}