package com.mercu.intrain.ui.course

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mercu.intrain.R
import com.mercu.intrain.model.Course
import com.mercu.intrain.API.ApiConfig
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
    private lateinit var enrollButton: TextView
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        courseTitle = findViewById(R.id.tvCourseTitle)
        courseDescription = findViewById(R.id.tvCourseDescription)
        courseProvider = findViewById(R.id.tvCourseProvider)
        courseUrl = findViewById(R.id.tvCourseUrl)
        enrollButton = findViewById(R.id.btnEnroll)
        backButton = findViewById(R.id.fabBack)

        course = intent.getParcelableExtra<Course>("course")!!

        courseTitle.text = course.title
        courseDescription.text = course.description
        courseProvider.text = "Provider: ${course.provider}"
        courseUrl.text = "URL: ${course.url}"
        sharedPrefHelper = SharedPrefHelper(this)

        backButton.setOnClickListener {
            onBackPressed()
        }

        val uid = sharedPrefHelper.getUid().toString()

        enrollButton.setOnClickListener {
            enrollCourse(uid,course.id)
        }
    }

    private fun enrollCourse(userId: String,courseId: String) {
        val enroll = EnrollmentRequest(userId,courseId)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ApiConfig.api.enrollCourse(enroll)
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailCourseActivity, "Successfully enrolled!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DetailCourseActivity, "Enrollment failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailCourseActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
