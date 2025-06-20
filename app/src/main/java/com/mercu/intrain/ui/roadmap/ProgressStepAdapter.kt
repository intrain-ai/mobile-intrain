package com.mercu.intrain.ui.roadmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.databinding.ItemProgressStepBinding
import com.mercu.intrain.model.ProgressStep

class ProgressStepAdapter(
    private val onCompleteClick: (ProgressStep) -> Unit
) : ListAdapter<ProgressStep, ProgressStepAdapter.ViewHolder>(ProgressStepDiffCallback()) {

    inner class ViewHolder(val binding: ItemProgressStepBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(step: ProgressStep) {
            binding.apply {
                tvStepOrder.text = "${step.stepOrder}."
                tvStepTitle.text = step.title
                tvStepDescription.text = step.description

                if (step.completed) {
                    cbComplete.isChecked = true
                    cbComplete.isEnabled = false
                    root.alpha = 0.6f
                } else {
                    cbComplete.isChecked = false
                    cbComplete.isEnabled = true
                    root.alpha = 1f
                    cbComplete.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) onCompleteClick(step)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProgressStepBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ProgressStepDiffCallback : DiffUtil.ItemCallback<ProgressStep>() {
    override fun areItemsTheSame(oldItem: ProgressStep, newItem: ProgressStep) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ProgressStep, newItem: ProgressStep) =
        oldItem == newItem
}