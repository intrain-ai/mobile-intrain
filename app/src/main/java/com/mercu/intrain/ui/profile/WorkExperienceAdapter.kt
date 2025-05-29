package com.mercu.intrain.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.databinding.ItemWorkExperienceBinding
import com.mercu.intrain.model.WorkExperience

class WorkExperienceAdapter(
    private var workExperiences: List<WorkExperience>,
    private val onEditClick: (WorkExperience) -> Unit,
    private val onDeleteClick: (WorkExperience) -> Unit
) : RecyclerView.Adapter<WorkExperienceAdapter.WorkExperienceViewHolder>() {

    private var isEditMode = false

    fun getEditMode(): Boolean = isEditMode

    inner class WorkExperienceViewHolder(private val binding: ItemWorkExperienceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workExperience: WorkExperience) {
            binding.jobTitleText.text = workExperience.jobTitle
            binding.companyNameText.text = workExperience.companyName
            binding.jobDescText.text = workExperience.jobDesc

            val dateRange = if (workExperience.isCurrent) {
                "${workExperience.startMonth}/${workExperience.startYear} - Present"
            } else {
                "${workExperience.startMonth}/${workExperience.startYear} - ${workExperience.endMonth}/${workExperience.endYear}"
            }
            binding.dateRangeText.text = dateRange

            // Set visibility of edit and delete buttons based on edit mode
            binding.editButton.visibility = if (isEditMode) ViewGroup.VISIBLE else ViewGroup.GONE
            binding.deleteButton.visibility = if (isEditMode) ViewGroup.VISIBLE else ViewGroup.GONE

            binding.editButton.setOnClickListener { onEditClick(workExperience) }
            binding.deleteButton.setOnClickListener { onDeleteClick(workExperience) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkExperienceViewHolder {
        val binding = ItemWorkExperienceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkExperienceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkExperienceViewHolder, position: Int) {
        holder.bind(workExperiences[position])
    }

    override fun getItemCount() = workExperiences.size

    fun updateWorkExperiences(newWorkExperiences: List<WorkExperience>) {
        workExperiences = newWorkExperiences
        notifyDataSetChanged()
    }

    fun setEditMode(enabled: Boolean) {
        isEditMode = enabled
        notifyDataSetChanged()
    }
} 