package com.ps.tokky.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.R
import com.ps.tokky.domain.usecases.FetchTokensUseCase
import com.ps.tokky.utils.FileHelper
import kotlinx.coroutines.launch
import org.json.JSONArray

private const val TAG = "ExportTokensViewModel"

class ExportTokensViewModel(
    private val fetchTokensUseCase: FetchTokensUseCase,
) : ViewModel() {

    private var _password = mutableStateOf("")
    val password: State<String> = _password
    private var _passwordError = mutableStateOf<String?>(null)
    val passwordError: State<String?> = _passwordError

    private var _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword
    private var _confirmPasswordError = mutableStateOf<String?>(null)
    val confirmPasswordError: State<String?> = _confirmPasswordError

    fun updatePassword(password: String) {
        _password.value = password
        _passwordError.value = null
    }

    fun updateConfirmPassword(password: String) {
        _confirmPassword.value = password
        _confirmPasswordError.value = null
    }

    fun verifyFields(context: Context): Boolean {
        val password = _password.value
        val confirmPassword = _confirmPassword.value

        if (password.isEmpty()) {
            _passwordError.value = context.getString(R.string.password_empty)
            return false
        }
        if (confirmPassword != password) {
            _confirmPasswordError.value = context.getString(R.string.password_mismatch)
            return false
        }

        return true
    }

    fun exportTokens(context: Context, filePath: Uri, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            fetchTokensUseCase()
                .fold(
                    onSuccess = { tokens ->
                        val exportData = JSONArray(tokens.map { /*TODO*/ }).toString()

                        FileHelper.writeToFile(
                            context = context,
                            uri = filePath,
                            content = exportData,
                            password = _password.value,
                            onFinished = onFinished
                        )
                    },
                    onFailure = {
                        //TODO: handle error
                    }
                )
        }
    }

    companion object {
        private const val TAG = "ExportTokensViewModel"
    }
}
