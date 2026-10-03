package com.homiee.helper.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.repository.ApiResult
import com.homiee.helper.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch

class PersonalInformationViewModel(private val repository: UserDetailsRepository) : ViewModel() {

    // ---- Submit state ----
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // ---- Form state (lives here so it survives navigating away and back) ----
    var fullName by mutableStateOf(""); private set
    var dob by mutableStateOf(""); private set          // "yyyy-MM-dd", sent to the API
    var idType by mutableStateOf(""); private set       // display label shown in dropdown
    var idTypeValue by mutableStateOf(""); private set  // backend value sent to API
    var idNumber by mutableStateOf(""); private set
    var frontCardUri by mutableStateOf<Uri?>(null); private set
    var backCardUri by mutableStateOf<Uri?>(null); private set

    val isAadhaar: Boolean get() = idTypeValue == "aadhaar"

    /** Continue stays disabled until every field is filled. */
    val isFormValid: Boolean
        get() = fullName.isNotBlank() &&
                dob.isNotBlank() &&
                idTypeValue.isNotBlank() &&
                idNumber.isNotBlank() &&
                (!isAadhaar || idNumber.length == 12) &&
                frontCardUri != null &&
                backCardUri != null

    fun onFullNameChange(v: String) { fullName = v }
    fun onDobChange(v: String) { dob = v }
    fun onFrontCardPicked(uri: Uri) { frontCardUri = uri }
    fun onBackCardPicked(uri: Uri) { backCardUri = uri }

    fun onIdTypeSelected(label: String) {
        val newValue = govIdTypeOptions.first { it.first == label }.second
        // Different ID types have different formats - start fresh on change.
        if (newValue != idTypeValue) idNumber = ""
        idType = label
        idTypeValue = newValue
    }

    fun onIdNumberChange(input: String) {
        idNumber = if (isAadhaar) input.filter { it.isDigit() }.take(12) else input
    }

    fun submit(onSuccess: () -> Unit) {
        if (!isFormValid || isLoading) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            // id_verified is never exposed to the user - the repository always
            // sends "false"; only the backend/admin can flip it after review.
            when (val result = repository.submitIdentity(
                fullName.trim(), dob, idTypeValue, idNumber.trim(), frontCardUri, backCardUri
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

    companion object {
        // Display label -> backend value expected by govt_id_type (Django choice field).
        val govIdTypeOptions = listOf(
            "Aadhar Card" to "aadhaar",
            "PAN Card" to "pan",
            "Voter ID" to "voter_id",
            "Driving License" to "driving_license"
        )
        val govIdTypes = govIdTypeOptions.map { it.first }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PersonalInformationViewModel(UserDetailsRepository(context.applicationContext)) as T
        }
    }
}