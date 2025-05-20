package com.mercu.intrain.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CarouselItemDecoration(
    private val margin: Int,
    private val peekHeight: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        with(outRect) {
            left = if (position == 0) margin else margin/2
            right = if (position == itemCount - 1) margin else margin/2
            top = peekHeight
            bottom = peekHeight
        }
    }
}