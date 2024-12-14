package com.ps.tokky.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.database.DBHelper
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.TokenExistsInDBException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokensViewModel @Inject constructor(
    private val db: DBHelper
) : ViewModel() {

    sealed class UIState<out T> {
        data object Loading : UIState<Nothing>()
        data class Success<out T>(val data: T, val isLoading: Boolean = false) : UIState<T>()
        data class Error(val message: String) : UIState<Nothing>()
    }

    private val _tokensState = MutableLiveData<UIState<List<TokenEntry>>>()
    val tokensState: LiveData<UIState<List<TokenEntry>>> = _tokensState

    var tokenToEdit: TokenEntry? = null
    var otpAuthUrl: String? = null

    fun fetchTokens() {
        viewModelScope.launch {
            _tokensState.value = UIState.Success(db.getAll(true))
        }
    }

    fun addToken(
        token: TokenEntry,
        requestCode: String,
        onComplete: (String) -> Unit,
        onTokenExists: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                db.add(token)
                onComplete(requestCode)
            } catch (exception: TokenExistsInDBException) {
                onTokenExists(requestCode)
            }
        }
    }

    fun updateToken(token: TokenEntry) {
        viewModelScope.launch {
            db.update(token)
        }
    }

    fun deleteToken(tokenId: String) {
        viewModelScope.launch {
            db.remove(tokenId)
        }
    }

    fun findToken(tokenId: String): Boolean {
        return db.getAll(false).find { it.id == tokenId } != null
    }

}
