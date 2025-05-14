//package com.mercu.intrain.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.mercu.intrain.model.Course
//import com.mercu.intrain.model.Enrollment
//import com.mercu.intrain.repository.CourseRepository
//import java.text.SimpleDateFormat
//import java.util.*
//
//class CourseViewModel(
//    private val repository: CourseRepository
//) : ViewModel() {
//
//    private val _unenrolledCourses = MutableLiveData<List<Course>>(emptyList())
//    val unenrolledCourses: LiveData<List<Course>> get() = _unenrolledCourses
//
//    private val _enrolledCourses = MutableLiveData<List<Enrollment>>(emptyList())
//    val enrolledCourses: LiveData<List<Enrollment>> get() = _enrolledCourses
//
//    private val _completedCourses = MutableLiveData<List<Enrollment>>(emptyList())
//    val completedCourses: LiveData<List<Enrollment>> get() = _completedCourses
//
//    private val _courseDetails = MutableLiveData<Map<String, Course>>(emptyMap())
//    val courseDetails: LiveData<Map<String, Course>> get() = _courseDetails
//
//    private val userId = "5f684a9c-2bcb-4e40-b176-03277860a9f7"
//
//    init {
//        loadDummyData()
//    }
//
//    private fun getNow(): String {
//        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//            .format(System.currentTimeMillis())
//    }
//
//    fun enroll(course: Course) {
//        _unenrolledCourses.value = _unenrolledCourses.value.orEmpty().filterNot { it.id == course.id }
//
//        val newEnrollment = Enrollment(
//            id = UUID.randomUUID().toString(),
//            userId = userId,
//            courseId = course.id,
//            status = "IN_PROGRESS",
//            progress = 0,
//            enrolledAt = getNow(),
//            completedAt = null,
//            course = course
//        )
//
//        _enrolledCourses.value = _enrolledCourses.value.orEmpty() + newEnrollment
//    }
//
//    fun complete(enrollment: Enrollment) {
//        _enrolledCourses.value = _enrolledCourses.value.orEmpty().filter { it.id != enrollment.id }
//
//        val completed = enrollment.copy(
//            status = "COMPLETED",
//            progress = 100,
//            completedAt = getNow()
//        )
//
//        _completedCourses.value = _completedCourses.value.orEmpty() + completed
//    }
//
//    fun unenroll(enrollment: Enrollment) {
//        _enrolledCourses.value = _enrolledCourses.value.orEmpty().filter { it.id != enrollment.id }
//        _completedCourses.value = _completedCourses.value.orEmpty().filter { it.id != enrollment.id }
//
//        _courseDetails.value?.get(enrollment.courseId)?.let {
//            _unenrolledCourses.value = _unenrolledCourses.value.orEmpty() + it
//        }
//    }
//
//    private fun loadDummyData() {
//        val c1 = Course("course-1", "Intro", "desc", "Academy A", "https://a.com", "2024-01-01")
//        val c2 = Course("course-2", "DSA", "desc", "Academy B", "https://b.com", "2024-01-02")
//        val c3 = Course("course-3", "ML", "desc", "Academy C", "https://c.com", "2024-01-03")
//        val c4 = Course("course-4", "Web Dev", "desc", "Academy D", "https://d.com", "2024-01-04")
//
//        val map = listOf(c1, c2, c3, c4).associateBy { it.id }
//        _courseDetails.value = map
//        _unenrolledCourses.value = listOf(c4)
//
//        val e1 = Enrollment(UUID.randomUUID().toString(), userId, c1.id, "IN_PROGRESS", 50, getNow(), null, c1)
//        val e2 = Enrollment(UUID.randomUUID().toString(), userId, c2.id, "COMPLETED", 100, getNow(), getNow(), c2)
//
//        _enrolledCourses.value = listOf(e1)
//        _completedCourses.value = listOf(e2)
//    }
//}
