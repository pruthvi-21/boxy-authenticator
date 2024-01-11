package com.ps.tokky.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class HalfSpaceSpan : ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        // Set the width of the space to half of the original size
        return (paint.measureText(" ") / 2).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // Draw nothing for the space character
    }
}