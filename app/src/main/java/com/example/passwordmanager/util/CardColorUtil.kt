package com.example.passwordmanager.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.example.passwordmanager.R

object CardColorUtil {

    fun getCardGradient(context: Context, position: Int): GradientDrawable {
        val gradients = context.resources.obtainTypedArray(R.array.card_gradient_colors)
        val gradientIndex = position % gradients.length()
        val gradientResId = gradients.getResourceId(gradientIndex, 0)
        val colors = context.resources.getIntArray(gradientResId)
        gradients.recycle()

        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            colors
        ).apply {
            cornerRadius = 16f * context.resources.displayMetrics.density
        }
    }
    
    /**
     * Calculate contrast between two colors
     */
    private fun calculateColorContrast(color1: Int, color2: Int): Double {
        val luminance1 = calculateLuminance(color1)
        val luminance2 = calculateLuminance(color2)
        
        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)
        
        return (lighter + 0.05) / (darker + 0.05)
    }
    
    /**
     * Calculate relative luminance of a color
     */
    private fun calculateLuminance(color: Int): Double {
        val red = Color.red(color) / 255.0
        val green = Color.green(color) / 255.0
        val blue = Color.blue(color) / 255.0
        
        val rsRGB = if (red <= 0.03928) red / 12.92 else Math.pow((red + 0.055) / 1.055, 2.4)
        val gsRGB = if (green <= 0.03928) green / 12.92 else Math.pow((green + 0.055) / 1.055, 2.4)
        val bsRGB = if (blue <= 0.03928) blue / 12.92 else Math.pow((blue + 0.055) / 1.055, 2.4)
        
        return 0.2126 * rsRGB + 0.7152 * gsRGB + 0.0722 * bsRGB
    }
    
    /**
     * Get a glassy version of the color with transparency
     */
    fun getGlassyColor(color: Int, alpha: Float = 0.85f): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alphaValue = (alpha * 255).toInt()
        return Color.argb(alphaValue, red, green, blue)
    }
    
    /**
     * Get a darker shade of the given color for text
     */
    fun getTextColorForBackground(backgroundColor: Int): Int {
        val red = Color.red(backgroundColor)
        val green = Color.green(backgroundColor)
        val blue = Color.blue(backgroundColor)
        
        // Calculate luminance
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
        
        // Return black for light backgrounds, white for dark backgrounds
        return if (luminance > 0.5) Color.BLACK else Color.WHITE
    }
    
    /**
     * Get a slightly darker version of the color for borders or accents
     */
    fun getDarkerShade(color: Int, factor: Float = 0.8f): Int {
        val red = (Color.red(color) * factor).toInt()
        val green = (Color.green(color) * factor).toInt()
        val blue = (Color.blue(color) * factor).toInt()
        return Color.rgb(red, green, blue)
    }
}
