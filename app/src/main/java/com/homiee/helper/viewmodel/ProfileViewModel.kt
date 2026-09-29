package com.homiee.helper.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.model.HelperProfileResponse
import com.homiee.helper.data.repository.ApiResult
import com.homiee.helper.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserDetailsRepository
) : ViewModel() {

    var profile by mutableStateOf<HelperProfileResponse?>(null); private set
    var isLoading by mutableStateOf(true); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        if (isLoading && profile != null) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = repository.getProfile()) {
                is ApiResult.Success -> profile = result.data
                is ApiResult.Error -> errorMessage = result.message
            }
            isLoading = false
        }
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(
                    UserDetailsRepository(context.applicationContext)
                ) as T
            }
        }
    }
}