package com.mercu.intrain.ui.course

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mercu.intrain.R
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.EnrollmentRequest
import com.mercu.intrain.sharedpref.SharedPrefHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailCourseActivity : AppCompatActivity() {

    private lateinit var courseTitle: TextView
    private lateinit var courseDescription: TextView
    private lateinit var courseProvider: TextView
    private lateinit var courseUrl: TextView
    private lateinit var backButton: FloatingActionButton
    private lateinit var actionButton: Button
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        courseTitle = findViewById(R.id.tvCourseTitle)
        courseDescription = findViewById(R.id.tvCourseDescription)
        courseProvider = findViewById(R.id.tvCourseProvider)
        courseUrl = findViewById(R.id.tvCourseUrl)
        actionButton = findViewById(R.id.btnEnroll)
        backButton = findViewById(R.id.fabBack)

        course = intent.getParcelableExtra<Course>("course")!!
        sharedPrefHelper = SharedPrefHelper(this)

        courseTitle.text = course.title
        courseDescription.text = course.description
        courseProvider.text = course.provider
        courseUrl.text = course.url

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val uid = sharedPrefHelper.getUid().toString()
        val isEnrolled = intent.getBooleanExtra("IS_ENROLLED", false)
        val isCompleted = intent.getBooleanExtra("IS_COMPLETED", false)

        when {
            isCompleted -> {
                actionButton.text = "Course Completed"
                actionButton.isEnabled = false
            }
            isEnrolled -> {
                actionButton.text = "Complete Course"
                actionButton.setOnClickListener {
                    completeCourse(uid, course.id)
                }
            }
            else -> {
                actionButton.text = "Enroll Now"
                actionButton.setOnClickListener {
                    enrollCourse(uid, course.id)
                }
            }
        }
    }

    private fun enrollCourse(userId: String, courseId: String) {
        val enroll = EnrollmentRequest(userId, courseId)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ApiConfig.api.enrollCourse(enroll)
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailCourseActivity, "Successfully enrolled!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@DetailCourseActivity, "Enrollment failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailCourseActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun completeCourse(userId: String, courseId: String) {
        val enroll = EnrollmentRequest(userId, courseId)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ApiConfig.api.completeCourse(enroll)
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailCourseActivity, "Congratulations! You have completed!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@DetailCourseActivity, "Completion failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailCourseActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}