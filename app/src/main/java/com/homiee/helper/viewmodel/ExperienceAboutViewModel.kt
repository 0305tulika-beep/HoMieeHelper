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

    // Submit state (shown in the footer).
    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    // Language catalog state (shown in the Languages section).
    var languages by mutableStateOf<List<LanguageDto>>(emptyList()); private set
    var isLoadingLanguages by mutableStateOf(true); private set
    var languagesError by mutableStateOf<String?>(null); private set

    init {
        loadLanguages()
    }

    fun loadLanguages() {
        if (isLoadingLanguages && languages.isNotEmpty()) return
        viewModelScope.launch {
            isLoadingLanguages = true
            languagesError = null
            when (val result = repository.getLanguages()) {
                is ApiResult.Success -> languages = result.data
                is ApiResult.Error -> languagesError = result.message
            }
            isLoadingLanguages = false
        }
    }

    /**
     * The screen already validates that the user picked a language or filled in
     * "Other", so languagesSpoken may be empty here (custom language only).
     */
    fun submit(
        yearsOfExperience: Int,
        languagesSpoken: List<Int>,
        about: String,
        onSuccess: () -> Unit
    ) {
        if (isLoading) return
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