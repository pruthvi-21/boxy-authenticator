package com.ps.tokky.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

private const val TAG = "RemovePasswordDialogViewModel"

class RemovePasswordDialogViewModel : ViewModel() {

    private val _password = mutableStateOf("")
    val password get() = _password.value

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun reset() {
        _password.value = ""
    }
}
