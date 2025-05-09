package com.mercu.intrain.ui.course

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mercu.intrain.API.ApiConfig
import com.mercu.intrain.API.ApiService
import com.mercu.intrain.R
import com.mercu.intrain.databinding.ActivityCourseBinding
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import com.mercu.intrain.model.EnrollmentRequest
import com.mercu.intrain.repository.CourseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseBinding
    private lateinit var tabLayout: TabLayout
    val courseRepository: CourseRepository by lazy { CourseRepository(ApiConfig.api) }
    private lateinit var viewPager: ViewPager2
    private lateinit var fab: FloatingActionButton

    // Shared data between tabs
    private val _unenrolledCourses = mutableListOf<Course>()
    private val _enrolledCourses = mutableListOf<Enrollment>()
    private val _completedCourses = mutableListOf<Enrollment>()
    private val _courseDetails = mutableMapOf<String, Course>()

    // Public getters for fragments
    val unenrolledCourses: List<Course> get() = _unenrolledCourses
    val enrolledCourses: List<Enrollment> get() = _enrolledCourses
    val completedCourses: List<Enrollment> get() = _completedCourses
    val courseDetails: Map<String, Course> get() = _courseDetails

    // Dummy user ID for testing
    private val userId = "5f684a9c-2bcb-4e40-b176-03277860a9f7"

    // Function to get current fragment
    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}")
    }

    // Function to update current fragment's adapter
    private fun updateCurrentFragment() {
        // Update all fragments to ensure consistency
        (getCurrentFragment() as? EnrolledCoursesFragment)?.updateAdapter()
        (getCurrentFragment() as? AvailableCoursesFragment)?.updateAdapter()
        (getCurrentFragment() as? CompletedCoursesFragment)?.updateAdapter()
    }

    // Function to update all fragments
    private fun updateAllFragments() {
        // Get all fragments from ViewPager2
        val pagerAdapter = viewPager.adapter as? CoursePagerAdapter
        pagerAdapter?.let { adapter ->
            for (i in 0 until adapter.itemCount) {
                val fragment = supportFragmentManager.findFragmentByTag("f$i")
                when (fragment) {
                    is EnrolledCoursesFragment -> fragment.updateAdapter()
                    is AvailableCoursesFragment -> fragment.updateAdapter()
                    is CompletedCoursesFragment -> fragment.updateAdapter()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        
        setupDummyData()
        setupTabLayout()
        setupWindowInsets()

        fab = binding.fab
        fab.setOnClickListener {
            // Navigate back to home/menu
            finish()
        }
    }

    private fun setupTabLayout() {
        // Set up ViewPager2 with adapter
        val pagerAdapter = CoursePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Enrolled"
                1 -> "Available"
                2 -> "Completed"
                else -> null
            }
        }.attach()
    }

    private fun setupDummyData() {
        // Clear existing data
        _unenrolledCourses.clear()
        _enrolledCourses.clear()
        _completedCourses.clear()
        _courseDetails.clear()

        // Create dummy courses
        val course1 = Course(
            id = "course-1",
            title = "Introduction to Programming",
            description = "Learn the basics of programming with Python. Perfect for beginners!",
            provider = "Code Academy",
            url = "https://codeacademy.com/intro",
            created_at = "2024-01-01T00:00:00"
        )

        val course2 = Course(
            id = "course-2",
            title = "Data Structures & Algorithms",
            description = "Master essential data structures and algorithms for technical interviews",
            provider = "Tech University",
            url = "https://techuniversity.com/dsa",
            created_at = "2024-01-02T00:00:00"
        )

        val course3 = Course(
            id = "course-3",
            title = "Machine Learning Fundamentals",
            description = "Introduction to machine learning concepts and practical applications",
            provider = "AI Institute",
            url = "https://aiinstitute.com/ml",
            created_at = "2024-01-03T00:00:00"
        )

        val course4 = Course(
            id = "course-4",
            title = "Modern Web Development",
            description = "Build responsive web applications using React, Node.js, and MongoDB",
            provider = "Web Dev Pro",
            url = "https://webdevpro.com/react",
            created_at = "2024-01-04T00:00:00"
        )

        val course5 = Course(
            id = "course-5",
            title = "Android Development with Kotlin",
            description = "Create professional Android apps using Kotlin and modern Android architecture",
            provider = "Mobile Masters",
            url = "https://mobilemasters.com/android",
            created_at = "2024-01-05T00:00:00"
        )

        val course6 = Course(
            id = "course-6",
            title = "Cloud Computing Essentials",
            description = "Learn AWS, Azure, and Google Cloud Platform fundamentals",
            provider = "Cloud Academy",
            url = "https://cloudacademy.com/essentials",
            created_at = "2024-01-06T00:00:00"
        )

        // Add all courses to courseDetails
        _courseDetails.putAll(mapOf(
            course1.id to course1,
            course2.id to course2,
            course3.id to course3,
            course4.id to course4,
            course5.id to course5,
            course6.id to course6
        ))

        // Add courses to unenrolled list (only courses that are not enrolled or completed)
        _unenrolledCourses.addAll(listOf(course4, course5, course6))

        // Create some initial enrollments with realistic dates and progress
        val enrollment1 = Enrollment(
            id = UUID.randomUUID().toString(),
            userId = userId,
            courseId = course1.id,
            status = "IN_PROGRESS",
            progress = 75,
            enrolledAt = "2024-01-15T10:00:00",
            completedAt = null,
            course = course1
        )

        val enrollment2 = Enrollment(
            id = UUID.randomUUID().toString(),
            userId = userId,
            courseId = course2.id,
            status = "COMPLETED",
            progress = 100,
            enrolledAt = "2024-01-10T09:00:00",
            completedAt = "2024-01-25T15:30:00",
            course = course2
        )

        val enrollment3 = Enrollment(
            id = UUID.randomUUID().toString(),
            userId = userId,
            courseId = course3.id,
            status = "IN_PROGRESS",
            progress = 30,
            enrolledAt = "2024-01-20T14:00:00",
            completedAt = null,
            course = course3
        )

        // Add enrollments to appropriate lists
        _enrolledCourses.addAll(listOf(enrollment1, enrollment3))
        _completedCourses.add(enrollment2)
    }

    // Commented out actual API call
    /*
    private fun loadInitialData() {
        lifecycleScope.launch {
            try {
                // Load all courses
                val coursesResponse = courseRepository.getAllCourses()
                if (coursesResponse.isSuccessful) {
                    coursesResponse.body()?.let { courses ->
                        unenrolledCourses.clear()
                        courseDetails.clear()
                        courses.forEach { course ->
                            courseDetails[course.id] = course
                            unenrolledCourses.add(course)
                        }
                    }
                }

                // Load user enrollments
                val enrollmentsResponse = courseRepository.getUserEnrollments(userId)
                if (enrollmentsResponse.isSuccessful) {
                    enrollmentsResponse.body()?.let { enrollments ->
                        enrolledCourses.clear()
                        completedCourses.clear()
                        enrollments.forEach { enrollment ->
                            if (enrollment.is_completed) {
                                completedCourses.add(enrollment)
                            } else {
                                enrolledCourses.add(enrollment)
                            }
                            // Remove enrolled courses from unenrolled list
                            courseDetails[enrollment.course_id]?.let { course ->
                                unenrolledCourses.remove(course)
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@CourseActivity, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    */

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun switchToTab(tabIndex: Int) {
        viewPager.currentItem = tabIndex
    }

    // Functions to move courses between states
    fun enrollCourse(course: Course) {
        // Remove from unenrolled courses
        _unenrolledCourses.removeIf { it.id == course.id }
        
        // Create new enrollment
        val enrollment = Enrollment(
            id = UUID.randomUUID().toString(),
            userId = userId,
            courseId = course.id,
            status = "IN_PROGRESS",
            progress = 0,
            enrolledAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(System.currentTimeMillis()),
            completedAt = null,
            course = course
        )
        
        // Add to enrolled courses
        _enrolledCourses.add(enrollment)
        
        // Force update all fragments
        updateAllFragments()
        
        // Switch to Enrolled tab
        viewPager.currentItem = 0
        
        Toast.makeText(this, "Course enrolled successfully", Toast.LENGTH_SHORT).show()
    }

    fun completeCourse(enrollment: Enrollment) {
        // Remove from enrolled courses
        _enrolledCourses.removeIf { it.id == enrollment.id }
        
        // Create completed enrollment
        val completedEnrollment = enrollment.copy(
            status = "COMPLETED",
            progress = 100,
            completedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(System.currentTimeMillis())
        )
        
        // Add to completed courses
        _completedCourses.add(completedEnrollment)
        
        // Force update all fragments
        updateAllFragments()
        
        // Switch to Completed tab
        viewPager.currentItem = 2
        
        Toast.makeText(this, "Course completed successfully", Toast.LENGTH_SHORT).show()
    }

    fun unenrollCourse(enrollment: Enrollment) {
        // Remove from appropriate list
        if (enrollment.status == "COMPLETED") {
            _completedCourses.removeIf { it.id == enrollment.id }
        } else {
            _enrolledCourses.removeIf { it.id == enrollment.id }
        }
        
        // Add back to unenrolled courses
        _courseDetails[enrollment.courseId]?.let { course ->
            _unenrolledCourses.add(course)
        }
        
        // Force update all fragments
        updateAllFragments()
        
        // Switch to Available tab
        viewPager.currentItem = 1
        
        Toast.makeText(this, "Course unenrolled successfully", Toast.LENGTH_SHORT).show()
    }

    fun updateCourseProgress(enrollment: Enrollment, progress: Int) {
        val index = _enrolledCourses.indexOfFirst { it.id == enrollment.id }
        if (index != -1) {
            val updatedEnrollment = enrollment.copy(progress = progress)
            _enrolledCourses[index] = updatedEnrollment
            updateAllFragments()
        }
    }
}