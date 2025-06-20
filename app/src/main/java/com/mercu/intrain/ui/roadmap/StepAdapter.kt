package com.mercu.intrain.ui.roadmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.databinding.ItemStepBinding
import com.mercu.intrain.model.Step

class StepAdapter : ListAdapter<Step, StepAdapter.ViewHolder>(StepDiffCallback()) {

    inner class ViewHolder(val binding: ItemStepBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(step: Step) {
            binding.apply {
                tvStepOrder.text = "${step.stepOrder}."
                tvStepTitle.text = step.title
                tvStepDescription.text = step.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStepBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class StepDiffCallback : DiffUtil.ItemCallback<Step>() {
    override fun areItemsTheSame(oldItem: Step, newItem: Step) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Step, newItem: Step) =
        oldItem == newItem
}