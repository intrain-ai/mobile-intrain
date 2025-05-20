package com.mercu.intrain.ui.news

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mercu.intrain.API.Article
import com.mercu.intrain.databinding.ItemNewsBinding

class NewsSliderAdapter(
    private val margin: Int
) : RecyclerView.ItemDecoration() {

    constructor(context: Context, marginDimen: Int) : this(
        context.resources.getDimensionPixelSize(marginDimen)
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        with(outRect) {
            left = if (position == 0) margin * 2 else margin
            right = if (position == itemCount - 1) margin * 2 else margin
        }
    }
}
