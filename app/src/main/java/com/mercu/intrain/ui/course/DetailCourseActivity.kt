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
    private var course: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        initializeViews()
        sharedPrefHelper = SharedPrefHelper(this)

        // Try to get course from intent extras
        course = intent.getParcelableExtra<Course>("course")
        val courseId = intent.getStringExtra("course_id")

        if (course != null) {
            // Course object was passed directly
            displayCourse(course!!)
        } else if (courseId != null) {
            // Course ID was passed, need to fetch course data
            loadCourseById(courseId)
        } else {
            // No course data provided
            showError("No course data provided")
            return
        }

        setupBackButton()
    }

    private fun initializeViews() {
        courseTitle = findViewById(R.id.tvCourseTitle)
        courseDescription = findViewById(R.id.tvCourseDescription)
        courseProvider = findViewById(R.id.tvCourseProvider)
        courseUrl = findViewById(R.id.tvCourseUrl)
        actionButton = findViewById(R.id.btnEnroll)
        backButton = findViewById(R.id.fabBack)
    }

    private fun displayCourse(course: Course) {
        courseTitle.text = course.title
        courseDescription.text = course.description
        courseProvider.text = course.provider
        courseUrl.text = course.url

        setupActionButton(course)
    }

    private fun loadCourseById(courseId: String) {
        // Show loading state
        courseTitle.text = "Loading..."
        courseDescription.text = ""
        courseProvider.text = ""
        courseUrl.text = ""
        actionButton.isEnabled = false
        actionButton.text = "Loading..."

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ApiConfig.api.getCourseDetails(courseId)
                if (response.isSuccessful) {
                    course = response.body()
                    if (course != null) {
                        displayCourse(course!!)
                    } else {
                        showError("Course not found")
                    }
                } else {
                    showError("Failed to load course: ${response.message()}")
                }
            } catch (e: Exception) {
                showError("Error loading course: ${e.message}")
            }
        }
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupActionButton(course: Course) {
        val uid = sharedPrefHelper.getUid()?.toString() ?: ""
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

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        courseTitle.text = "Error"
        courseDescription.text = message
        courseProvider.text = ""
        courseUrl.text = ""
        actionButton.isEnabled = false
        actionButton.text = "Error"
    }

    private fun enrollCourse(userId: String, courseId: String) {
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

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
        if (userId.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

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