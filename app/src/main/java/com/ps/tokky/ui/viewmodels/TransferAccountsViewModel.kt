package com.ps.tokky.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ps.tokky.data.models.TokenEntry
import com.ps.tokky.data.repositories.TokensRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class TransferAccountsViewModel @Inject constructor(
    private val tokensRepository: TokensRepository
) : ViewModel() {

    sealed class UIState<out T> {
        data object Loading : UIState<Nothing>()
        data class Success<out T>(val insertedAccounts: T, val duplicateAccounts: T) : UIState<T>()
    }

    private val _importAccountsState = MutableLiveData<UIState<List<TokenEntry>>>()
    val importAccountsState: LiveData<UIState<List<TokenEntry>>> = _importAccountsState

    fun importAccounts(accounts: List<TokenEntry>) {
        viewModelScope.launch {
            _importAccountsState.value = UIState.Loading
            val existingAccounts = tokensRepository.getAllTokens()

            val newAccounts = ArrayList<TokenEntry>()
            val duplicateAccounts = ArrayList<TokenEntry>()
            accounts.forEach { newAccount ->
                val existingAccount = existingAccounts.find { newAccount.name == it.name }
                if (existingAccount == null) {
                    newAccounts.add(newAccount)
                } else {
                    duplicateAccounts.add(newAccount)
                }
            }
            tokensRepository.insertAccounts(newAccounts)
            _importAccountsState.value = UIState.Success(newAccounts, duplicateAccounts)
        }
    }
}
