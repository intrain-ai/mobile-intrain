package com.mercu.intrain.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mercu.intrain.R
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment

class EnrolledCoursesAdapter(
    private var enrollList: List<Enrollment>,
    private val onClick: ((Course) -> Unit)? = null
) : RecyclerView.Adapter<EnrolledCoursesAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvCourseTitle)
        val provider: TextView = itemView.findViewById(R.id.tvProvider)
        val enrolledDate: TextView = itemView.findViewById(R.id.tvEnrolledDate)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.progressCourse)
        val btnAction: MaterialButton = itemView.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val enrollment = enrollList[position]

        holder.title.text = enrollment.title
        holder.provider.text = course.provider
        holder.enrolledDate.text = "Enrolled: ${enrollment.enrolled_at}"
        holder.status.text = if (enrollment.is_completed) "Completed" else "In Progress"
        holder.progressBar.progress = enrollment.progress

        holder.btnAction.text = "Detail"
        holder.btnAction.setOnClickListener {
            onClick?.invoke(course) // Handle click event
        }
    }

    override fun getItemCount(): Int = enrollList.size  // Count based on enrollList

    // Update enrollment data
    fun updateDataEnroll(newEnrollList: List<Enrollment>) {
        enrollList = newEnrollList
        notifyDataSetChanged()
    }
}
