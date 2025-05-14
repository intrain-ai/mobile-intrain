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
    private var enrollList: List<Enrollment>,
    private val onClick: ((Course) -> Unit)? = null
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private lateinit var sharedPrefHelper: SharedPrefHelper

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
        sharedPrefHelper = SharedPrefHelper(holder.itemView.context)
        holder.title.text = course.title
        holder.provider.text = course.provider

        // Nonaktifkan komponen yang tidak digunakan saat hanya menampilkan data
        holder.enrolledDate.visibility = View.GONE
        holder.status.visibility = View.GONE
        holder.progressBar.visibility = View.GONE

        holder.btnAction.text = "Detail"
        holder.btnAction.setOnClickListener {
            onClick?.invoke(course)
        }
    }

    override fun getItemCount(): Int = courseList.size

    fun updateData(newCourses: List<Course>) {
        courseList = newCourses
        notifyDataSetChanged()
    }

    fun updateDataEnroll(newCourses: List<Enrollment>) {
        enrollList = newCourses
        notifyDataSetChanged()
    }
}
