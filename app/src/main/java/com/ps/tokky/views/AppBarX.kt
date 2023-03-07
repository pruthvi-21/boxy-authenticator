package com.ps.tokky.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.ps.tokky.R
import kotlin.math.abs

class AppBarX @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppBarLayout(context, attrs, defStyleAttr) {

    private val expandedAlphaStart = 0.1f
    private val expandedAlphaEnd = 0.7f
    private val collapsedAlphaStart = 0.3f
    private val collapsedAlphaEnd = 0.8f

    init {
        setBackgroundColor(context.getColor(R.color.surface_color))
        setExpanded(false)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        var collapsingToolbarX: CollapsingToolbarX? = null
        for (i in 0..childCount) {
            val view = getChildAt(i)
            if (view is CollapsingToolbarX) {
                collapsingToolbarX = view
                break
            }
        }

        if (collapsingToolbarX != null) {
            val expandedView = collapsingToolbarX.expandedContentPane
            val collapsedView = collapsingToolbarX.collapsedTitleView

            val offsetChangeListener = OnOffsetChangedListener { _, verticalOffset ->
                val pos = abs(verticalOffset.toFloat()) / totalScrollRange
                if (pos == 0.0f) {
                    expandedView.alpha = 1f
                    collapsedView.alpha = 0f
                }
                if (pos == 1.0f) {
                    collapsedView.alpha = 1f
                    expandedView.alpha = 0f
                }
                if (pos >= collapsedAlphaStart && pos < collapsedAlphaEnd) {
                    val percentage =
                        (pos - collapsedAlphaStart) / (collapsedAlphaEnd - collapsedAlphaStart)
                    collapsedView.alpha = percentage
                }
                if (pos >= expandedAlphaStart && pos < expandedAlphaEnd) {
                    val percentage: Float = pos / (expandedAlphaEnd - expandedAlphaStart)
                    expandedView.alpha = 1 - percentage
                }
            }
            addOnOffsetChangedListener(offsetChangeListener)
        }
    }
}