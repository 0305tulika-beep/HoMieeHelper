package com.homiee.helper.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.model.LanguageDto
import com.homiee.helper.data.repository.ApiResult
import com.homiee.helper.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch

class ExperienceAboutViewModel(
    private val repository: UserDetailsRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    // Real languages from the backend catalog (with real ids).
    var languages by mutableStateOf<List<LanguageDto>>(emptyList()); private set
    var isLoadingLanguages by mutableStateOf(true); private set

    init {
        loadLanguages()
    }

    fun loadLanguages() {
        viewModelScope.launch {
            isLoadingLanguages = true
            errorMessage = null
            when (val result = repository.getLanguages()) {
                is ApiResult.Success -> languages = result.data
                is ApiResult.Error -> errorMessage = result.message
            }
            isLoadingLanguages = false
        }
    }

    fun submit(
        yearsOfExperience: Int,
        languagesSpoken: List<Int>,
        about: String,
        onSuccess: () -> Unit
    ) {
        // Only insist on a language when the backend actually has some to pick from.
        if (languagesSpoken.isEmpty() && languages.isNotEmpty()) {
            errorMessage = "Please select at least one language."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = repository.submitExperience(yearsOfExperience, languagesSpoken, about)) {
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
                return ExperienceAboutViewModel(
                    UserDetailsRepository(context.applicationContext)
                ) as T
            }
        }
    }
}