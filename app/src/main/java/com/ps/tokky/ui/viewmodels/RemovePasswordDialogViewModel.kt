package com.ps.tokky.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "RemovePasswordDialogViewModel"

@HiltViewModel
class RemovePasswordDialogViewModel @Inject constructor() : ViewModel() {

    private val _password = mutableStateOf("")
    val password get() = _password.value

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun reset() {
        _password.value = ""
    }
}
