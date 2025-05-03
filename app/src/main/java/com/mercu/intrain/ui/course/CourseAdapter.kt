package com.mercu.intrain.ui.course

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.R
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import java.text.SimpleDateFormat
import java.util.Locale

class CourseAdapter(
    private val enrollments: List<Enrollment>,
    private val courseDetails: Map<String, Course>
) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseTitle: TextView = itemView.findViewById(R.id.tvCourseTitle)
        val tvProvider: TextView = itemView.findViewById(R.id.tvProvider)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvEnrolledDate: TextView = itemView.findViewById(R.id.tvEnrolledDate)
        val btnAction: Button = itemView.findViewById(R.id.btnAction)
        val progressCourse: ProgressBar = itemView.findViewById(R.id.progressCourse)
    }

    private var actionClickListener: ((Enrollment, Course) -> Unit)? = null

    fun setOnActionClickListener(listener: (Enrollment, Course) -> Unit) {
        actionClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val enrollment = enrollments[position]
        val course = courseDetails[enrollment.course_id] ?: return

        with(holder) {
            tvCourseTitle.text = course.title
            tvProvider.text = course.provider
            tvEnrolledDate.text = "Enrolled: ${formatDate(enrollment.enrolled_at)}"

            if (enrollment.is_completed) {
                progressCourse.progress = 100
                progressCourse.progressTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                tvStatus.text = "Completed"
                tvStatus.setBackgroundColor(Color.parseColor("#4CAF50"))
                btnAction.text = "Unenroll"
            } else {
                progressCourse.progress = 25
                progressCourse.progressTintList = ColorStateList.valueOf(Color.parseColor("#FFC107"))
                tvStatus.text = "In Progress"
                tvStatus.setBackgroundColor(Color.parseColor("#FFC107"))
                btnAction.text = "Complete"
            }

            btnAction.setOnClickListener {
                actionClickListener?.invoke(enrollment, course)
            }
        }
    }

    override fun getItemCount() = enrollments.size

    private fun formatDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            formatter.format(parser.parse(dateString) ?: dateString)
        } catch (e: Exception) {
            dateString
        }
    }
}