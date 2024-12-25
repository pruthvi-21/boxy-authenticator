package com.ps.tokky.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RemovePasswordDialogViewModel @Inject constructor() : ViewModel() {

    private val _password = mutableStateOf("")
    val password get() = _password.value

    fun updatePassword(value: String) {
        val numericInput = value.filter { it.isDigit() }
        _password.value = numericInput
    }

    fun reset() {
        _password.value = ""
    }
}
