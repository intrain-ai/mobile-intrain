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
import com.mercu.intrain.model.EnrollMock

class EnrolledCoursesAdapter(
    private var enrollList: List<EnrollMock>,
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

        holder.title.text = enrollment.course_title
        holder.provider.text = enrollment.provider

        if (enrollment.is_completed) {
            holder.status.text = "Completed"
            holder.status.visibility = View.VISIBLE

            holder.enrolledDate.visibility = View.VISIBLE
            holder.enrolledDate.text = "Completed: ${enrollment.completed_at}"

            holder.progressBar.visibility = View.GONE

            holder.btnAction.text = "See Certificate"
        } else {
            holder.status.text = "In Progress"
            holder.status.visibility = View.VISIBLE

            holder.enrolledDate.visibility = View.VISIBLE
            holder.enrolledDate.text = "Enrolled: ${enrollment.enrolled_at}"

            holder.progressBar.visibility = View.VISIBLE
            holder.progressBar.progress = enrollment.progress

            holder.btnAction.text = "Detail"
        }

        holder.btnAction.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = enrollList.size

    fun updateDataEnroll(allEnrollments: List<EnrollMock>) {
        enrollList = allEnrollments.filter { it.completed_at == null }
        notifyDataSetChanged()
    }

    fun updateDataComplete(allEnrollments: List<EnrollMock>) {
        enrollList = allEnrollments.filter { it.completed_at != null }
        notifyDataSetChanged()
    }
}
