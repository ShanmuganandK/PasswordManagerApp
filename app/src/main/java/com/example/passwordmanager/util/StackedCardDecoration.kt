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
        
        // Draw rounded shadows for stacked effect
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            
            if (position >= 0) {
                // Draw rounded shadow close to card
                val shadowOffset = position * 1
                val cornerRadius = 16f // Match card corner radius
                
                // Create shadow rect slightly offset from card
                val shadowRect = RectF(
                    child.left + shadowOffset + 2f,
                    child.top + shadowOffset + 2f,
                    child.right + shadowOffset + 2f,
                    child.bottom + shadowOffset + 2f
                )
                
                paint.alpha = if (isDarkTheme) {
                    (60 - position * 6).coerceAtLeast(15) // Adjusted for dark theme
                } else {
                    (40 - position * 4).coerceAtLeast(8)
                }
                
                // Draw rounded rectangle shadow
                c.drawRoundRect(shadowRect, cornerRadius, cornerRadius, paint)
            }
        }
    }
} 