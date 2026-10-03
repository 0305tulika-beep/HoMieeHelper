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
import java.util.Locale

class AddressInformationViewModel(private val repository: UserDetailsRepository) : ViewModel() {

    // ---- Submit state ----
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // ---- Form state (lives here so it survives navigating away and back) ----
    var street by mutableStateOf(""); private set
    var city by mutableStateOf(""); private set
    var state by mutableStateOf(""); private set
    var pincode by mutableStateOf(""); private set
    var latitude by mutableStateOf(""); private set
    var longitude by mutableStateOf(""); private set

    /** Continue stays disabled until every field is filled. */
    val isFormValid: Boolean
        get() = street.isNotBlank() &&
                city.isNotBlank() &&
                state.isNotBlank() &&
                pincode.length == 6 &&
                latitude.isNotBlank() &&
                longitude.isNotBlank()

    fun onStreetChange(v: String) { street = v }
    fun onCityChange(v: String) { city = v }
    fun onStateChange(v: String) { state = v }
    fun onPincodeChange(v: String) { pincode = v }

    fun onLocationFetched(lat: Double, lng: Double) {
        latitude = String.format(Locale.US, "%.6f", lat)
        longitude = String.format(Locale.US, "%.6f", lng)
    }

    fun submit(onSuccess: () -> Unit) {
        if (!isFormValid || isLoading) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = repository.submitAddress(
                street.trim(), state, city.trim(), pincode, latitude, longitude
            )) {
                is ApiResult.Success -> {
                    isLoading = false
                    onSuccess()
                }
                is ApiResult.Error -> {
                    isLoading = false
                    errorMessage = result.message
                }
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddressInformationViewModel(UserDetailsRepository(context.applicationContext)) as T
        }
    }
}