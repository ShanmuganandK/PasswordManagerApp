package com.example.passwordmanager.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StackedCardDecoration : RecyclerView.ItemDecoration() {
    
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    
    private var isDarkTheme = false
    
    fun setDarkTheme(isDark: Boolean) {
        isDarkTheme = isDark
        paint.color = if (isDark) {
            0x40FFFFFF.toInt() // White shadow for dark theme
        } else {
            0x20000000.toInt() // Black shadow for light theme
        }
    }
    
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        
        // Minimal offset for tight stacking effect
        outRect.left = position * 1
        outRect.right = position * 1
        outRect.top = position * 1
        outRect.bottom = 8 // Reduced bottom margin
    }
    
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        // No-op
    }
}
