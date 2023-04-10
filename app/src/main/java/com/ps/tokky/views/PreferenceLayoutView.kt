package com.ps.tokky.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ps.tokky.R

class PreferenceLayoutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val titleView: TextView
    private val summaryView: TextView
    private val iconView: ImageView

    init {
        inflate(context, R.layout.preference_layout_view, this)

        titleView = findViewById(R.id.title)
        summaryView = findViewById(R.id.summary)
        iconView = findViewById(R.id.icon)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PreferenceLayoutView)
        val title = typedArray.getString(R.styleable.PreferenceLayoutView_title)
        val summary = typedArray.getString(R.styleable.PreferenceLayoutView_summary)
        val icon = typedArray.getResourceId(R.styleable.PreferenceLayoutView_icon, 0)

        title?.let { setTitle(it) }
        setSummary(summary)
        setIcon(icon)

        typedArray.recycle()
    }

    fun setTitle(@StringRes res: Int) {
        setTitle(context.getString(res))
    }

    private fun setTitle(title: String) {
        titleView.text = title
    }

    fun setSummary(@StringRes res: Int) {
        setSummary(context.getString(res))
    }

    fun setSummary(summary: String?) {
        if (summary == null) {
            summaryView.visibility = View.GONE
            return
        }

        summaryView.visibility = View.VISIBLE
        summaryView.text = summary
    }

    fun setIcon(@DrawableRes icon: Int) {
        if (icon == 0) {
            iconView.visibility = View.GONE
            return
        }

        iconView.visibility = View.VISIBLE
        iconView.setImageResource(icon)
    }
}