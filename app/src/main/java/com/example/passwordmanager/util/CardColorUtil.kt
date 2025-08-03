package com.example.passwordmanager.util

import android.content.Context
import android.graphics.Color
import com.example.passwordmanager.R

object CardColorUtil {
    
    /**
     * Get a color for a card based on its position
     * This ensures each card gets a unique color in a cycling pattern with high contrast
     */
    fun getCardColor(context: Context, position: Int): Int {
        val colors = context.resources.obtainTypedArray(R.array.card_colors)
        val colorIndex = position % colors.length()
        val color = colors.getColor(colorIndex, Color.GRAY)
        colors.recycle()
        return color
    }
    
    /**
     * Get a color with enhanced contrast from the previous card
     */
    fun getCardColorWithContrast(context: Context, position: Int, previousColor: Int? = null): Int {
        val colors = context.resources.obtainTypedArray(R.array.card_colors)
        val totalColors = colors.length()
        
        if (previousColor == null) {
            val color = colors.getColor(0, Color.GRAY)
            colors.recycle()
            return color
        }
        
        // Find the best contrasting color
        var bestColor = colors.getColor(0, Color.GRAY)
        var maxContrast = 0.0
        
        for (i in 0 until totalColors) {
            val currentColor = colors.getColor(i, Color.GRAY)
            val contrast = calculateColorContrast(previousColor, currentColor)
            if (contrast > maxContrast) {
                maxContrast = contrast
                bestColor = currentColor
            }
        }
        
        colors.recycle()
        return bestColor
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
     * Get a color for a card based on its title/content
     * This ensures the same content always gets the same color
     */
    fun getCardColorForContent(context: Context, content: String): Int {
        val colors = context.resources.obtainTypedArray(R.array.card_colors)
        val hash = content.hashCode()
        val colorIndex = Math.abs(hash) % colors.length()
        val color = colors.getColor(colorIndex, Color.GRAY)
        colors.recycle()
        return color
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