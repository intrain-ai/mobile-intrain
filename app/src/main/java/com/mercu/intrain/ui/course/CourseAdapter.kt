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
import com.mercu.intrain.model.EnrollmentItem

class CourseAdapter(
    private var courseList: List<Course>,
    private var enrollList: List<EnrollmentItem>,
    private val onClick: ((Course) -> Unit)? = null
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvCourseTitle)
        val provider: TextView = itemView.findViewById(R.id.tvProvider)
        val enrolledDate: TextView = itemView.findViewById(R.id.tvEnrolledDate)
        val statusView: TextView = itemView.findViewById(R.id.tvStatus)
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
        holder.title.text = course.title
        holder.provider.text = course.provider

        // --- LOGIKA YANG DIPERBAIKI ---
        // Cari status pendaftaran untuk kursus ini berdasarkan course.id
        val enrollmentStatus = enrollList.find { it.courseId == course.id }

        if (enrollmentStatus != null) {
            // Jika kursus ini ditemukan di daftar pendaftaran pengguna
            holder.statusView.visibility = View.VISIBLE
            if (enrollmentStatus.isCompleted == true) {
                // Status: Sudah Selesai
                holder.statusView.text = "Completed"
                holder.btnAction.text = "View Again"
            } else {
                // Status: Sedang Berjalan
                holder.statusView.text = "In Progress"
                holder.btnAction.text = "Continue"
            }
        } else {
            // Jika kursus ini tidak ada di daftar pendaftaran pengguna
            holder.statusView.visibility = View.GONE
            holder.btnAction.text = "View Details"
        }

        // Sembunyikan elemen yang tidak relevan di halaman ini
        holder.enrolledDate.visibility = View.GONE
        holder.progressBar.visibility = View.GONE

        // Aksi klik selalu sama: buka halaman detail
        holder.itemView.setOnClickListener {
            onClick?.invoke(course)
        }
        holder.btnAction.setOnClickListener {
            onClick?.invoke(course)
        }
    }

    override fun getItemCount(): Int = courseList.size

    // Fungsi ini untuk mengupdate daftar pendaftaran
    fun updateUserEnrollments(newEnrollList: List<EnrollmentItem>) {
        enrollList = newEnrollList
        notifyDataSetChanged()
    }

    // Fungsi ini untuk mengupdate daftar semua kursus
    fun updateData(newCourses: List<Course>) {
        courseList = newCourses
        notifyDataSetChanged()
    }
}