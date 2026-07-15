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
import kotlin.random.Random

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel(context: Context) : ViewModel() {

    private val repository = AuthRepository(context)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Set once register() succeeds — SignUpScreen reads this to know which
    // identifier the OTP was actually sent to.
    var registeredEmail: String = ""
        private set

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        password2: String
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val username = generateUsername(email)
            when (
                val result = repository.register(
                    firstName, lastName, email, username, password, password2
                )
            ) {
                is ApiResult.Success -> {
                    registeredEmail = email
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }

    // No username field in the UI — derive one from the email's local part
    // and pad it with a random suffix to cut down on collisions. Capped at
    // 15 characters total (base + suffix), matching the API's limit. If the
    // server still rejects it as taken (409), the person just needs to
    // retry, which will roll a new suffix.
    private fun generateUsername(email: String): String {
        val suffix = Random.nextInt(1000, 9999).toString()
        val maxBaseLength = 15 - suffix.length
        val base = email.substringBefore("@")
            .lowercase()
            .filter { it.isLetterOrDigit() }
            .ifBlank { "helper" }
            .take(maxBaseLength)
        return "$base$suffix"
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(context.applicationContext) as T
        }
    }
}