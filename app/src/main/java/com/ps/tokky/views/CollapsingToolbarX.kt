package com.ps.tokky.views

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.ps.tokky.R
import com.ps.tokky.utils.Utils

class CollapsingToolbarX @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyleAttr) {

    private val expandedTitleView: TextView
    val collapsedTitleView: TextView

    val expandedContentPane: FrameLayout
    val toolbar: Toolbar

    init {
        expandedContentPane = FrameLayout(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            ).apply {
                bottomMargin =
                    context.resources.getDimension(R.dimen.collapsing_toolbar_expanded_frame_margin_bottom)
                        .toInt()
                collapseMode = LayoutParams.COLLAPSE_MODE_PARALLAX
                parallaxMultiplier = 0.5F
            }

            expandedTitleView = TextView(context).apply {
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }
                setText(R.string.app_name)
                gravity = Gravity.CENTER
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Design_CollapsingToolbar_Expanded)
            }

            addView(expandedTitleView)
        }

        toolbar = Toolbar(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                Utils.getDimenFromAttr(context, com.google.android.material.R.attr.actionBarSize)
            ).apply {
                collapseMode = LayoutParams.COLLAPSE_MODE_PIN
                gravity = Gravity.BOTTOM
                topMargin = context.resources.getDimension(R.dimen.toolbar_margin_top).toInt()

            }
            elevation = 0f
            contentInsetStartWithNavigation = 0
            title = " "

            collapsedTitleView = TextView(context).apply {
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setPadding(0, 0, 0, 3)
                }
                setText(R.string.app_name)
                gravity = Gravity.CENTER
                setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Widget_ActionBar_Title)
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            }

            addView(collapsedTitleView)
        }

        addView(expandedContentPane)
        addView(toolbar)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val params = layoutParams
        if (params is AppBarLayout.LayoutParams) {
            params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                    AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP or
                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        }
    }

    fun setTitle(title: String) {
        collapsedTitleView.text = title
        expandedTitleView.text = title
    }

    fun setTitle(@StringRes titleRes: Int) {
        setTitle(context.getString(titleRes))
    }
}