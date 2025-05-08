package com.mercu.intrain.ui.course

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.mercu.intrain.R
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import com.mercu.intrain.repository.CourseRepository
import java.text.SimpleDateFormat
import java.util.*

class CourseAdapter(
    private val items: MutableList<Any>,
    private val courseDetails: Map<String, Course>,
    private val courseRepository: CourseRepository,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    private var onEnrollClickListener: ((Course) -> Unit)? = null
    private var onStateChangeListener: ((CourseState) -> Unit)? = null

    enum class CourseState {
        ENROLLED,
        COMPLETED,
        UNENROLLED
    }

    fun setOnEnrollClickListener(listener: (Course) -> Unit) {
        onEnrollClickListener = listener
    }

    fun setOnStateChangeListener(listener: (CourseState) -> Unit) {
        onStateChangeListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseTitle: TextView = itemView.findViewById(R.id.tvCourseTitle)
        val tvProvider: TextView = itemView.findViewById(R.id.tvProvider)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvEnrolledDate: TextView = itemView.findViewById(R.id.tvEnrolledDate)
        val btnAction: Button = itemView.findViewById(R.id.btnAction)
        val progressCourse: ProgressBar = itemView.findViewById(R.id.progressCourse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        when (item) {
            is Course -> {
                // Handle unenrolled course
                with(holder) {
                    tvCourseTitle.text = item.title
                    tvProvider.text = item.provider
                    tvEnrolledDate.visibility = View.GONE
                    progressCourse.visibility = View.GONE
                    tvStatus.visibility = View.GONE
                    btnAction.text = "Enroll"
                    btnAction.setOnClickListener {
                        val activity = itemView.context as? CourseActivity
                        activity?.enrollCourse(item)
                    }
                }
            }
            is Enrollment -> {
                // Handle enrolled course
                val course = courseDetails[item.courseId] ?: return
                with(holder) {
                    tvCourseTitle.text = course.title
                    tvProvider.text = course.provider
                    tvEnrolledDate.text = "Enrolled: ${formatDate(item.enrolledAt)}"
                    tvEnrolledDate.visibility = View.VISIBLE
                    progressCourse.visibility = View.VISIBLE
                    tvStatus.visibility = View.VISIBLE

                    if (item.status == "COMPLETED") {
                        progressCourse.progress = 100
                        progressCourse.progressTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                        tvStatus.text = "Completed"
                        tvStatus.setBackgroundColor(Color.parseColor("#4CAF50"))
                        btnAction.text = "Unenroll"
                        btnAction.setOnClickListener {
                            val activity = itemView.context as? CourseActivity
                            activity?.unenrollCourse(item)
                        }
                    } else {
                        progressCourse.progress = item.progress
                        progressCourse.progressTintList = ColorStateList.valueOf(Color.parseColor("#FFC107"))
                        tvStatus.text = "In Progress"
                        tvStatus.setBackgroundColor(Color.parseColor("#FFC107"))
                        
                        btnAction.text = "Complete"
                        btnAction.setOnClickListener {
                            val activity = itemView.context as? CourseActivity
                            activity?.completeCourse(item)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount() = items.size

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