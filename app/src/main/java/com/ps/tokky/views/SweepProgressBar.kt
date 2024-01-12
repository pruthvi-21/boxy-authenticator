package com.ps.tokky.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.R
import com.google.android.material.color.MaterialColors

class SweepProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val colorPrimary = MaterialColors.getColor(this, R.attr.colorPrimary)
    private val colorSurface = MaterialColors.getColor(this, R.attr.colorSurfaceContainerHigh)
    private val colorError = ResourcesCompat.getColor(resources, com.ps.tokky.R.color.color_danger, null)

    private val paintBackground: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = colorPrimary
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
        if (swipeAngle > 360f * 0.7) {
            paintBackground.color = colorError
        } else paintBackground.color = colorPrimary
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), (radius - 1).toFloat(), paintBackground)
        canvas.drawArc(rectProgress, START_ANGLE, swipeAngle, true, paintProgress)
    }

    fun setMax(max: Int) {
        this.max = max
    }

    fun startAnim(start: Int, end: Int, duration: Long, callback: (() -> Unit)? = null) {
        val startPercent = start * 100f / max
        val startAngle = startPercent * 360 / 100
        val targetPercent = end * 100f / max
        val targetAngle = targetPercent * 360 / 100

        progressAnimator?.cancel()
        progressAnimator = ObjectAnimator
            .ofFloat(this, "swipeAngle", startAngle, targetAngle).also {
                it.setDuration(duration)
                it.interpolator = LinearInterpolator()
                it?.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        callback?.invoke()
                    }
                })
                it.start()
            }
    }

    companion object {
        private const val START_ANGLE = -90f
    }
}
