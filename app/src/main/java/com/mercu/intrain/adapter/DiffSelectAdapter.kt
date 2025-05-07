package com.mercu.intrain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.databinding.ItemDifficultyBinding
import com.mercu.intrain.model.Difficulty


class DiffSelectAdapter(private val list: List<Difficulty>) :
    RecyclerView.Adapter<DiffSelectAdapter.DifficultyViewHolder>() {

    inner class DifficultyViewHolder(val binding: ItemDifficultyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DifficultyViewHolder {
        val binding = ItemDifficultyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DifficultyViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: DifficultyViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvDesc.text = item.description
        holder.binding.imageView.setImageResource(item.imageRes)
    }
}
