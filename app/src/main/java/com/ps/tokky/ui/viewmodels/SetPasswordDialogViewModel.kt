package com.ps.tokky.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ps.tokky.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetPasswordDialogViewModel @Inject constructor() : ViewModel() {

    private val _password = mutableStateOf("")
    val password get() = _password.value

    private val _passwordError = mutableStateOf<String?>(null)
    val passwordError get() = _passwordError.value

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword get() = _confirmPassword.value

    private val _confirmPasswordError = mutableStateOf<String?>(null)
    val confirmPasswordError get() = _confirmPasswordError.value

    private val _showPassword = mutableStateOf(false)
    val showPassword get() = _showPassword.value

    fun updatePassword(value: String) {
        val numericInput = value.filter { it.isDigit() }
        _password.value = numericInput
        _passwordError.value = null
        _confirmPasswordError.value = null
    }

    fun updateConfirmPassword(value: String) {
        val numericInput = value.filter { it.isDigit() }
        _confirmPassword.value = numericInput
        _passwordError.value = null
        _confirmPasswordError.value = null
    }

    fun validatePasswords(context: Context): Boolean {
        _passwordError.value = when {
            password.length < 6 -> context.getString(R.string.password_min_length_error)
            password.isEmpty() -> context.getString(R.string.password_empty)
            else -> null
        }

        _confirmPasswordError.value = when {
            confirmPassword != password -> context.getString(R.string.password_mismatch)
            else -> null
        }

        return passwordError == null && confirmPasswordError == null
    }

    fun reset() {
        _password.value = ""
        _passwordError.value = null
        _confirmPassword.value = ""
        _confirmPasswordError.value = null
    }

    fun updateShowPassword(show: Boolean) {
        _showPassword.value = show
    }
}
