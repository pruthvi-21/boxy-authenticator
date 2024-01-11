package com.ps.tokky.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.R
import com.ps.tokky.utils.Utils

class SweepProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val colorSecondary = Utils.getColorFromAttr(context, R.attr.colorSecondary, Color.WHITE)
    private val colorSurface = Utils.getColorFromAttr(context, R.attr.colorSurface, Color.BLACK)
    private val colorError = ResourcesCompat.getColor(resources, com.ps.tokky.R.color.color_danger, null)

    private val paintBackground: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = colorSecondary
    }
    private val paintProgress = Paint(paintBackground).apply {
        color = colorSurface
    }
    private val rectProgress = RectF()

    private var max = 100
    private var centerX = 0
    private var centerY = 0
    private var radius = 0
    private var swipeAngle = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var progressAnimator: ObjectAnimator? = null
    private var initialLoad = true

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        centerX = viewWidth / 2
        centerY = viewHeight / 2
        radius = Math.min(viewWidth, viewHeight) / 2

        rectProgress.left = (centerX - radius).toFloat()
        rectProgress.top = (centerY - radius).toFloat()
        rectProgress.right = (centerX + radius).toFloat()
        rectProgress.bottom = (centerY + radius).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), (radius - 1).toFloat(), paintBackground)
        canvas.drawArc(rectProgress, START_ANGLE, swipeAngle, true, paintProgress)
    }

    fun setMax(max: Int) {
        this.max = max
    }

    fun setProgress(progress: Int, animate: Boolean = true) {
        //-1 just to correct the sweep animation
        val percentage = progress * 100f / (max - 1)
        val targetAngle = percentage * 360 / 100

        progressAnimator?.cancel()

        val duration = if (!initialLoad) if (animate) 1000L else 0L else 0L

        progressAnimator = ObjectAnimator.ofFloat(this, "swipeAngle", swipeAngle, targetAngle)
            .setDuration(duration)

        progressAnimator?.interpolator = LinearInterpolator()
        progressAnimator?.start()

        paintBackground.color = if (percentage < 70) colorSecondary
        else colorError

        if (initialLoad) initialLoad = false
    }

    companion object {
        private const val START_ANGLE = -90f
    }
}
