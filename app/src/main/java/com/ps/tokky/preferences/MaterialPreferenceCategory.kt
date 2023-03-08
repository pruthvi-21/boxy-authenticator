package com.ps.tokky.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.ps.tokky.R


class MaterialPreferenceCategory @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @SuppressLint("RestrictedApi")
    defStyleAttr: Int = TypedArrayUtils.getAttr(
        context, androidx.preference.R.attr.preferenceCategoryStyle,
        android.R.attr.preferenceCategoryStyle
    ),
    defStyleRes: Int = 0
) : PreferenceCategory(context, attrs, defStyleAttr, defStyleRes) {

    init {
        layoutResource = R.layout.preference_category_layout
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val titleView = holder.findViewById(android.R.id.title) as TextView

        if (title == null) {
            titleView.visibility = View.GONE
        } else {
            titleView.text = title
            titleView.visibility = View.VISIBLE
        }

        val count = preferenceCount
        if (count == 1) {
            getPreference(0).layoutResource = R.layout.preference_layout_single
        } else if (count > 1) {
            getPreference(0).layoutResource = R.layout.preference_layout_top
            getPreference(count - 1).layoutResource = R.layout.preference_layout_bottom
        }
    }

}