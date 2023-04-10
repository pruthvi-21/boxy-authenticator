package com.ps.tokky.utils

import android.text.Editable
import android.text.TextWatcher

open class TextWatcherAdapter : TextWatcher {
    override fun beforeTextChanged(data: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(data: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {}
}