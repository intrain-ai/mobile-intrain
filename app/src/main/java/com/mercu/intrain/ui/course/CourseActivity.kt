package com.mercu.intrain.ui.course

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mercu.intrain.R
import com.mercu.intrain.databinding.ActivityCourseBinding
import com.mercu.intrain.model.Course
import com.mercu.intrain.model.Enrollment
import java.text.SimpleDateFormat
import java.util.*

class CourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseBinding
    private val dummyEnrollments = mutableListOf<Enrollment>()
    private val dummyCourses = mutableListOf<Course>()

    // Gamification System
    private data class UserStats(
        var currentStreak: Int = 0,
        var longestStreak: Int = 0,
        var lastActiveDate: String = "",
        var totalXP: Int = 0,
        var currentLevel: Int = 1
    )

    private val userStats = UserStats()
    private val streakManager = StreakManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDummyData()
        setupRecyclerView()
        setupGamificationUI()
        setupWindowInsets()
    }

    private fun setupDummyData() {
        val course1 = Course(
            id = UUID.randomUUID().toString(),
            title = "C# Programming Fundamentals",
            description = "Learn basic C# programming concepts",
            provider = "Udemy",
            url = "https://example.com/csharp",
            created_at = "2024-01-01T00:00:00"
        )

        val course2 = Course(
            id = UUID.randomUUID().toString(),
            title = "Web Development Basics",
            description = "HTML, CSS, and JavaScript fundamentals",
            provider = "Coursera",
            url = "https://example.com/webdev",
            created_at = "2024-02-01T00:00:00"
        )

        dummyCourses.addAll(listOf(course1, course2))

        dummyEnrollments.addAll(listOf(
            Enrollment(
                id = UUID.randomUUID().toString(),
                user_id = "user-123",
                course_id = course1.id,
                enrolled_at = "2024-03-01T10:00:00",
                completed_at = null,
                is_completed = false
            ),
            Enrollment(
                id = UUID.randomUUID().toString(),
                user_id = "user-123",
                course_id = course2.id,
                enrolled_at = "2024-03-15T14:30:00",
                completed_at = "2024-04-01T09:15:00",
                is_completed = true
            ),
            Enrollment(
                id = UUID.randomUUID().toString(),
                user_id = "user-123",
                course_id = course1.id,
                enrolled_at = "2024-04-05T16:45:00",
                completed_at = null,
                is_completed = false
            ),
            Enrollment(
                id = UUID.randomUUID().toString(),
                user_id = "user-123",
                course_id = course2.id,
                enrolled_at = "2024-04-20T12:20:00",
                completed_at = null,
                is_completed = false
            )
        ))
    }

    private fun setupGamificationUI() {
        updateGamificationDisplay()
    }


    private fun setupRecyclerView() {
        val adapter = CourseAdapter(
            enrollments = dummyEnrollments,
            courseDetails = dummyCourses.associateBy { it.id }
        ).apply {
            setOnActionClickListener { enrollment, course ->
                handleCourseAction(enrollment, course)
            }
        }

        binding.rvCourses.apply {
            layoutManager = LinearLayoutManager(this@CourseActivity)
            this.adapter = adapter
        }
    }

    private fun handleCourseAction(enrollment: Enrollment, course: Course) {
        when (enrollment.is_completed) {
            true -> unenrollCourse(enrollment)
            false -> completeCourse(enrollment, course)
        }
    }

    // Ubah cara update XP dan level
    private fun completeCourse(enrollment: Enrollment, course: Course) {
        val updatedEnrollment = enrollment.copy(
            is_completed = true,
            completed_at = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(Date())
        )

        dummyEnrollments.replaceAll { if (it.id == enrollment.id) updatedEnrollment else it }

        // Update gamification
        streakManager.updateStreak()
        userStats.totalXP += 100
        userStats.currentLevel = (userStats.totalXP / 100) + 1 // Perbaikan perhitungan level

        // Update UI
        binding.rvCourses.adapter?.notifyDataSetChanged()
        updateGamificationDisplay()
        checkLevelUp()

        Toast.makeText(this, "${course.title} completed! +100 XP", Toast.LENGTH_SHORT).show()
    }

    private fun updateGamificationDisplay() {
        val newProgress = userStats.totalXP % 100
        val currentLevel = (userStats.totalXP / 100) + 1
        val nextLevelXP = userStats.currentLevel * 100
        val currentLevelXP = (userStats.currentLevel - 1) * 100
        val progress = userStats.totalXP - currentLevelXP
        val maxProgress = nextLevelXP - currentLevelXP

        binding.apply {
            tvCurrentStreak.text = "🔥 ${userStats.currentStreak} Days"
            tvLongestStreak.text = "🏆 Longest: ${userStats.longestStreak}"
            tvLevel.text = "Level $currentLevel"

            progressBarXP.max = maxProgress
            progressBarXP.progress = progress
            tvXP.text = "$progress/$maxProgress XP"
        }
    }

    private fun checkLevelUp() {
        if (userStats.totalXP % 100 == 0 && userStats.totalXP > 0) {
            val level = (userStats.totalXP / 100) + 1
            Toast.makeText(this, "🌟 Level Up! Now Level $level", Toast.LENGTH_LONG).show()
        }
    }

    private fun unenrollCourse(enrollment: Enrollment) {
        dummyEnrollments.replaceAll { if (it.id == enrollment.id) enrollment.copy(is_completed = false) else it }
        binding.rvCourses.adapter?.notifyDataSetChanged()
        Toast.makeText(this, "Course unenrolled", Toast.LENGTH_SHORT).show()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    inner class StreakManager {
        fun updateStreak() {
            val today = getCurrentDate()

            if (isConsecutiveDay(userStats.lastActiveDate, today)) {
                userStats.currentStreak++
                userStats.longestStreak = maxOf(userStats.longestStreak, userStats.currentStreak)
            } else {
                userStats.currentStreak = 1
            }

            userStats.lastActiveDate = today
        }

        private fun getCurrentDate(): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }

        private fun isConsecutiveDay(prevDate: String, currDate: String): Boolean {
            return try {
                val prev = SimpleDateFormat("yyyy-MM-dd").parse(prevDate)
                val curr = SimpleDateFormat("yyyy-MM-dd").parse(currDate)
                (curr.time - prev.time) == 86400000L // 1 hari dalam milidetik
            } catch (e: Exception) {
                false
            }
        }
    }
}