package com.homiee.helper.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

    // ---- Form state (lives here so it survives navigating away and back) ----
    var experience by mutableStateOf(""); private set   // display label
    val selectedLanguageIds = mutableStateListOf<Int>()
    var otherChecked by mutableStateOf(false); private set
    var otherLanguage by mutableStateOf(""); private set
    var aboutYou by mutableStateOf(""); private set

    /** Continue stays disabled until every section is filled. */
    val isFormValid: Boolean
        get() {
            val otherValid = !otherChecked || otherLanguage.isNotBlank()
            val languagesValid =
                (selectedLanguageIds.isNotEmpty() || (otherChecked && otherLanguage.isNotBlank())) && otherValid
            return experience.isNotBlank() && languagesValid && aboutYou.isNotBlank()
        }

    fun onExperienceSelected(label: String) { experience = label }

    fun onLanguageToggled(id: Int, checked: Boolean) {
        if (checked) {
            if (id !in selectedLanguageIds) selectedLanguageIds.add(id)
        } else {
            selectedLanguageIds.remove(id)
        }
    }

    fun onOtherCheckedChange(checked: Boolean) {
        otherChecked = checked
        if (!checked) otherLanguage = ""
    }

    fun onOtherLanguageChange(v: String) { otherLanguage = v }

    fun onAboutChange(v: String) { if (v.length <= 300) aboutYou = v }

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
     * languagesSpoken may be empty (custom "Other" language only) - isFormValid
     * already guarantees the user picked a language or filled in "Other".
     */
    fun submit(onSuccess: () -> Unit) {
        if (isLoading || !isFormValid) return
        val years = experienceOptions.first { it.first == experience }.second
        val about = if (otherChecked && otherLanguage.isNotBlank()) {
            "${aboutYou.trim()}\n\nAlso speaks: ${otherLanguage.trim()}"
        } else aboutYou.trim()

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = repository.submitExperience(years, selectedLanguageIds.toList(), about)) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.Error -> errorMessage = result.message
            }
            isLoading = false
        }
    }

    companion object {
        // Display label -> backend years_of_experience (Int).
        val experienceOptions = listOf(
            "Less than 1 year" to 0,
            "1-2 years" to 1,
            "3-5 years" to 3,
            "5+ years" to 5
        )
        val experienceLabels = experienceOptions.map { it.first }

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