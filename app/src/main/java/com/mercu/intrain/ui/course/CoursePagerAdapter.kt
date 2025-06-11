package com.mercu.intrain.ui.course

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mercu.intrain.ui.course.AvailableCoursesFragment

class CoursePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AvailableCoursesFragment()
            1 -> EnrolledCoursesFragment()
            2 -> CompletedCoursesFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
} 