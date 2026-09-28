package com.homiee.helper.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.repository.ApiResult
import com.homiee.helper.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch

class AvailabilityViewModel(
    private val repository: UserDetailsRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    fun submit(workingDays: List<String>, startTime: String, endTime: String, onSuccess: () -> Unit) {
        if (workingDays.isEmpty()) {
            errorMessage = "Please select at least one working day."
            return
        }
        if (startTime == "00:00:00" || endTime == "00:00:00") {
            errorMessage = "Please select both a start and end time."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = repository.submitAvailability(workingDays, startTime, endTime)) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.Error -> errorMessage = result.message
            }
            isLoading = false
        }
    }

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AvailabilityViewModel(
                    UserDetailsRepository(context.applicationContext)
                ) as T
            }
        }
    }
}