package com.ps.tokky.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.R
import com.ps.tokky.data.repositories.TokensRepository
import com.ps.tokky.utils.FileHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class ExportTokensViewModel @Inject constructor(
    private val tokensRepository: TokensRepository,
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
            val exportData = JSONArray(tokensRepository.getAllTokens().map { it.toExportJson() }).toString()

            FileHelper.writeToFile(
                context = context,
                uri = filePath,
                content = exportData,
                password = _password.value,
                onFinished = onFinished
            )
        }
    }

    companion object {
        private const val TAG = "ExportTokensViewModel"
    }
}
