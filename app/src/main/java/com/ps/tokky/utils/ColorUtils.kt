package com.ps.tokky.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt

object ColorUtils {
    @ColorInt
    fun primaryTintedBackground(context: Context): Int {
        val color1 = getThemeColor(context, com.google.android.material.R.attr.colorSurface)
        val color2 = getThemeColor(context, com.google.android.material.R.attr.colorPrimary)
        val c1 = blend(color1, color2, 0.89f)
        return blend(c1, Color.WHITE, 0.97f)
    }

    fun blend(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRation: Float = 1f - ratio
        val r: Float = Color.red(color1) * ratio + Color.red(color2) * inverseRation
        val g: Float = Color.green(color1) * ratio + Color.green(color2) * inverseRation
        val b: Float = Color.blue(color1) * ratio + Color.blue(color2) * inverseRation
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    fun getThemeColor(context: Context, colorAttr: Int): Int {
        val arr = context.theme.obtainStyledAttributes(intArrayOf(colorAttr))
        val colorValue = arr.getColor(0, -1)
        arr.recycle()
        return colorValue
    }
}