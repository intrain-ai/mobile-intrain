package com.mercu.intrain.ui.test

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.mercu.intrain.R

class CustomSegmentedTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TabLayout(context, attrs) {

    private val tabTitles = mutableListOf<String>()

    fun setTabs(titles: List<String>) {
        tabTitles.clear()
        tabTitles.addAll(titles)

        removeAllTabs()
        tabTitles.forEach {
            addTab(newTab().setCustomView(R.layout.custom_tab))
        }

        updateTabs()
        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                updateTabView(tab, selected = true)
            }

            override fun onTabUnselected(tab: Tab) {
                updateTabView(tab, selected = false)
            }

            override fun onTabReselected(tab: Tab) {}
        })
    }

    private fun updateTabs() {
        tabTitles.forEachIndexed { index, title ->
            val tab = getTabAt(index)
            tab?.customView?.let { customView ->
                val textView = customView.findViewById<TextView>(R.id.tabText)
                textView.text = title
                if (index == selectedTabPosition) {
                    customView.background = ContextCompat.getDrawable(context, R.drawable.tab_selected)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.purple_700))
                } else {
                    customView.background = ContextCompat.getDrawable(context, R.drawable.tab_unselected)
                    textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                }
            }
        }
    }

    private fun updateTabView(tab: Tab, selected: Boolean) {
        tab.customView?.let { customView ->
            val textView = customView.findViewById<TextView>(R.id.tabText)
            if (selected) {
                customView.background = ContextCompat.getDrawable(context, R.drawable.tab_selected)
                textView.setTextColor(ContextCompat.getColor(context, R.color.purple_700))
            } else {
                customView.background = ContextCompat.getDrawable(context, R.drawable.tab_unselected)
                textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
        }
    }
}
