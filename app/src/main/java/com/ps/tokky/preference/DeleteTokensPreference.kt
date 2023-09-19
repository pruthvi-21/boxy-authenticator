package com.ps.tokky.preference

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ps.tokky.R
import com.ps.tokky.database.DBHelper
import com.ps.tokky.databinding.DialogTitleDeleteWarningBinding

class DeleteTokensPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : Preference(context, attrs) {
    init {
        setOnPreferenceClickListener {
            val titleViewBinding = DialogTitleDeleteWarningBinding.inflate(LayoutInflater.from(context))

            titleViewBinding.title.setText(R.string.dialog_title_kill)
            MaterialAlertDialogBuilder(context, R.style.DialogKillTheme)
                .setCustomTitle(titleViewBinding.root)
                .setMessage(R.string.dialog_message_kill)
                .setPositiveButton(R.string.dialog_kill_positive_btn) { _, _ ->
                    DBHelper.getInstance(context).kill()
                }
                .setNegativeButton(R.string.dialog_cancel, null)
                .show()
            true
        }
    }
}