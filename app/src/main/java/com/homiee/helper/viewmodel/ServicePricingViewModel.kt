package com.homiee.helper.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.model.ServiceDto
import com.homiee.helper.data.model.ServicePriceItem
import com.homiee.helper.data.repository.ApiResult
import com.homiee.helper.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch

class ServicesPricingViewModel(
    private val repository: UserDetailsRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    // Real services from the backend catalog (with real ids).
    var services by mutableStateOf<List<ServiceDto>>(emptyList()); private set
    var isLoadingServices by mutableStateOf(true); private set

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            isLoadingServices = true
            errorMessage = null
            when (val result = repository.getServices()) {
                is ApiResult.Success -> services = result.data
                is ApiResult.Error -> errorMessage = result.message
            }
            isLoadingServices = false
        }
    }

    fun submit(servicePrices: List<ServicePriceItem>, onSuccess: () -> Unit) {
        if (servicePrices.isEmpty()) {
            errorMessage = "Please select at least one service."
            return
        }
        if (servicePrices.any { (it.price_per_hour.toDoubleOrNull() ?: 0.0) <= 0.0 }) {
            errorMessage = "Please enter a valid price for each selected service."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = repository.submitServices(servicePrices)) {
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
                return ServicesPricingViewModel(
                    UserDetailsRepository(context.applicationContext)
                ) as T
            }
        }
    }
}