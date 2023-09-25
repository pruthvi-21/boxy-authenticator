package com.ps.tokky.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import com.ps.tokky.R
import com.ps.tokky.databinding.LayoutExpandableFabBinding

class ExpandableFab @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = LayoutExpandableFabBinding.inflate(LayoutInflater.from(context), this, true)
    val fabAddNew = binding.fabAddNew
    val fabQr = binding.fabQr
    val fabManual = binding.fabManual
    val fabScrim = binding.scrim

    private val fadeInAnimation by lazy {
        AlphaAnimation(SCRIM_FADE_MIN_ALPHA, SCRIM_FADE_MAX_ALPHA).apply {
            duration = ANIM_DURATION
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {
                    fabScrim.visibility = View.VISIBLE
                }
            })
        }
    }
    private val fadeOutAnimation by lazy {
        AlphaAnimation(SCRIM_FADE_MAX_ALPHA, SCRIM_FADE_MIN_ALPHA).apply {
            duration = ANIM_DURATION
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    fabScrim.visibility = View.GONE
                }
            })
        }
    }

    private val slideUpFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.slide_up_fab_content)
    }
    private val slideDownFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.slide_down_fab_content)
    }
    private val clockwiseRotateAnim by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_fab_clockwise)
    }
    private val antiClockwiseRotateAnim by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_fab_anticlockwise)
    }

    var isFabExpanded = false
        set(value) {
            field = value
            if (value) {
                fabQr.startAnimation(slideUpFabAnim)
                fabManual.startAnimation(slideUpFabAnim)
                fabScrim.startAnimation(fadeInAnimation)
                fabAddNew.startAnimation(clockwiseRotateAnim)
            } else {
                fabQr.startAnimation(slideDownFabAnim)
                fabManual.startAnimation(slideDownFabAnim)
                fabScrim.startAnimation(fadeOutAnimation)
                fabAddNew.startAnimation(antiClockwiseRotateAnim)
            }
        }

    init {
        fabAddNew.setOnClickListener {
            isFabExpanded = !isFabExpanded
        }

        fabScrim.setOnClickListener {
            if (isFabExpanded) isFabExpanded = false
        }
    }

    companion object {
        private const val ANIM_DURATION = 150L
        private const val SCRIM_FADE_MIN_ALPHA = 0f
        private const val SCRIM_FADE_MAX_ALPHA = .6f

    }

}