package com.mercu.intrain.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mercu.intrain.R
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import com.mercu.intrain.sharedpref.SharedPrefHelper

class CourseAdapter(
    private var courseList: List<Course>,
    private var enrollList: List<Enrollment>,  // pastikan ini dideklarasikan dengan benar
    private val onClick: ((Course) -> Unit)? = null
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    // ViewHolder untuk CourseAdapter
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
        val course = courseList[position]
        val enrollment = enrollList.getOrNull(position)  // Mengambil item dari enrollList berdasarkan index

        holder.title.text = course.title
        holder.provider.text = course.provider

        // Mengatur komponen jika enrollList ada
        enrollment?.let {
            holder.enrolledDate.text = "Enrolled: ${it.enrolled_at}"
            holder.status.text = if (it.is_completed) "Completed" else "In Progress"

        }

        holder.btnAction.text = "Detail"
        holder.btnAction.setOnClickListener {
            onClick?.invoke(course)  // Memanggil aksi klik jika ada
        }
    }

    override fun getItemCount(): Int = courseList.size  // Hanya menghitung berdasarkan courseList

    fun updateDataEnroll(newEnrollList: List<Enrollment>) {
        enrollList = newEnrollList  // Update enrollList dengan data baru
        notifyDataSetChanged()  // Memberitahu adapter untuk me-refresh RecyclerView
    }

    fun updateData(newCourses: List<Course>) {
        courseList = newCourses  // Update courseList dengan data baru
        notifyDataSetChanged()  // Memberitahu adapter untuk me-refresh RecyclerView
    }
}
