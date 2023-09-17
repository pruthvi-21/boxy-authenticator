package com.ps.tokky.preference

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceFragmentCompat
import com.ps.tokky.activities.transfer.imports.ImportActivity
import com.ps.tokky.utils.Constants

class ImportAccountsPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : Preference(context, attrs) {

    fun setupListeners(preferenceFragment: PreferenceFragmentCompat) {
        val importAccountsLauncher =
            preferenceFragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                refresh(true)
            }

        val readFileLauncher =
            preferenceFragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val filePath = it.data?.data
                if (filePath != null) {
                    importAccountsLauncher.launch(
                        Intent(context, ImportActivity::class.java)
                            .putExtra(
                                ImportActivity.INTENT_EXTRA_KEY_FILE_PATH,
                                filePath.toString()
                            )
                    )
                }
            }

        onPreferenceClickListener = OnPreferenceClickListener {
            val filePickerIntent = Intent().apply {
                type = Constants.FILE_MIME_TYPE
                action = Intent.ACTION_GET_CONTENT
            }

            readFileLauncher.launch(filePickerIntent)
            true
        }
    }


}