package com.homiee.helper.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.repository.ApiResult
import com.homiee.helper.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AccountAction { LOGOUT, DEACTIVATE, DELETE }

data class AccountActionUiState(
    val activeAction: AccountAction? = null,   // which action is currently in-flight, if any
    val errorMessage: String? = null,
    val actionCompleted: Boolean = false
) {
    val isLoading: Boolean get() = activeAction != null
}

class AccountActionViewModel(context: Context) : ViewModel() {

    private val repository = AuthRepository(context)

    private val _uiState = MutableStateFlow(AccountActionUiState())
    val uiState: StateFlow<AccountActionUiState> = _uiState.asStateFlow()

    fun logout() {
        _uiState.value = _uiState.value.copy(activeAction = AccountAction.LOGOUT, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.logout()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(activeAction = null, actionCompleted = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(activeAction = null, errorMessage = result.message)
            }
        }
    }

    fun deactivateAccount(password: String) {
        _uiState.value = _uiState.value.copy(activeAction = AccountAction.DEACTIVATE, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.deactivateAccount(password)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(activeAction = null, actionCompleted = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(activeAction = null, errorMessage = result.message)
            }
        }
    }

    fun deleteAccount(password: String) {
        _uiState.value = _uiState.value.copy(activeAction = AccountAction.DELETE, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.deleteAccount(password)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(activeAction = null, actionCompleted = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(activeAction = null, errorMessage = result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = AccountActionUiState()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AccountActionViewModel(context.applicationContext) as T
        }
    }
}