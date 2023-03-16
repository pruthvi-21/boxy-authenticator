package com.ps.tokky.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ps.tokky.R
import com.ps.tokky.utils.Utils

class PinField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val radius = context.resources.getDimension(R.dimen.lockscreen_pin_dot_radius)
    private val gap = context.resources.getDimension(R.dimen.lockscreen_pin_dot_gap)

    private val pinLength = 4
    var currentLength = 0
        set(value) {
            field = value
            invalidate()
        }

    val paintActive = Paint().apply {
        color = Utils.getThemeColorFromAttr(context, com.google.android.material.R.attr.colorPrimary)
    }

    val paintInactive = Paint().apply {
        color = Utils.getThemeColorFromAttr(context, com.google.android.material.R.attr.colorSurfaceVariant)
    }

    override fun onDraw(canvas: Canvas?) {
        val padding = gap / 2

        for (i in 0 until pinLength) {
            canvas?.drawCircle(
                padding + radius + (2 * i) * (padding + radius),
                radius + padding,
                radius,
                if (i < currentLength) paintActive else paintInactive
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dotSize = radius * 2

        val totalWidth = (dotSize * pinLength) + (pinLength * gap)
        val totalHeight = dotSize + gap
        setMeasuredDimension(totalWidth.toInt(), totalHeight.toInt())
    }
}