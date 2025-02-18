package com.boxy.authenticator.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

private const val TAG = "SetPasswordDialogViewModel"

class SetPasswordDialogViewModel : ViewModel() {

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
        _password.value = value
        _passwordError.value = null
        _confirmPasswordError.value = null
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
        _passwordError.value = null
        _confirmPasswordError.value = null
    }

    fun validatePasswords(): Boolean {
        _passwordError.value = when {
            password.isEmpty() -> "Password can't be empty"
            password.length < 6 -> "Password should have at least 6 characters"
            else -> null
        }

        _confirmPasswordError.value = when {
            confirmPassword != password -> "Password didn't matched"
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
