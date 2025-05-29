package com.mercu.intrain.ui.course

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mercu.intrain.databinding.ActivityCourseBinding
import com.mercu.intrain.ui.test.CustomSegmentedTabLayout

class CourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseBinding
    private lateinit var tabLayout: CustomSegmentedTabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout = binding.tabLayout as CustomSegmentedTabLayout
        viewPager = binding.viewPager

        viewPager.adapter = CoursePagerAdapter(this)

        tabLayout.setTabs(listOf("Enrolled", "Available", "Completed"))

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
}

