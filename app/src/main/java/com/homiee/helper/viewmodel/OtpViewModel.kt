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

data class OtpUiState(
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val isResending: Boolean = false,
    val errorMessage: String? = null
)

class OtpViewModel(context: Context) : ViewModel() {

    private val repository = AuthRepository(context)

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    fun verifyOtp(identifier: String, otp: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.verifyOtp(identifier, otp)) {
                is ApiResult.Success -> {
                    // isVerified flips → OtpScreen's LaunchedEffect calls
                    // onVerifyClick(), which the nav host routes into the
                    // onboarding form flow (PersonalInformation).
                    _uiState.value = _uiState.value.copy(isLoading = false, isVerified = true)
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

    fun resendOtp(identifier: String) {
        _uiState.value = _uiState.value.copy(isResending = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.resendOtp(identifier)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(isResending = false)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isResending = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = OtpUiState()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return OtpViewModel(context.applicationContext) as T
        }
    }
}