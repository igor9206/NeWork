package ru.netology.nework.adapter.tools

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AvatarItemDecoration(
    private val negativeSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = -negativeSpacing
    }
}