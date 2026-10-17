package com.homiee.helper.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.homiee.helper.data.model.HelperProfileResponse
import com.homiee.helper.data.model.LanguageDto
import com.homiee.helper.data.repository.ApiResult
import com.homiee.helper.data.repository.UserDetailsRepository
import kotlinx.coroutines.launch

/**
 * Lets an already-onboarded helper change their saved details.
 *
 * It is pre-filled from the profile the Profile screen already loaded, and on
 * Save it re-sends ONLY the sections that actually changed, through the same
 * endpoints onboarding uses (address, experience + languages, availability).
 * If one section fails, the ones before it stay saved, the error is shown, and
 * pressing Save again only retries what is still unsaved.
 *
 * Validation only looks at the sections that were changed, so an old value that
 * wouldn't pass today's rules can never block saving something unrelated.
 */
class EditProfileViewModel(
    private val repository: UserDetailsRepository
) : ViewModel() {

    var isReady by mutableStateOf(false); private set
    var isSaving by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    // ── Address ──────────────────────────────────────────────────────────────
    var houseNo by mutableStateOf(""); private set
    var city by mutableStateOf(""); private set
    var state by mutableStateOf(""); private set
    var pincode by mutableStateOf(""); private set

    // Not editable here - sent back unchanged (the address endpoint wants them).
    private var latitude = ""
    private var longitude = ""

    // ── Experience, languages, about ─────────────────────────────────────────
    var yearsOfExperience by mutableStateOf(0); private set
    var about by mutableStateOf(""); private set
    var selectedLanguageIds by mutableStateOf<Set<Int>>(emptySet()); private set

    var languages by mutableStateOf<List<LanguageDto>>(emptyList()); private set
    var isLoadingLanguages by mutableStateOf(false); private set
    var languagesError by mutableStateOf<String?>(null); private set

    // ── Availability ─────────────────────────────────────────────────────────
    var workingDays by mutableStateOf<Set<String>>(emptySet()); private set   // "mon".."sun"
    var startTime by mutableStateOf(""); private set                          // "HH:mm:ss"
    var endTime by mutableStateOf(""); private set                            // "HH:mm:ss"

    // What the server currently has. Compared against the form to detect changes.
    private data class Snapshot(
        val houseNo: String,
        val city: String,
        val state: String,
        val pincode: String,
        val yearsOfExperience: Int,
        val about: String,
        val languageIds: Set<Int>,
        val workingDays: Set<String>,
        val startTime: String,
        val endTime: String
    ) {
        fun addressDiffers(o: Snapshot) =
            houseNo != o.houseNo || city != o.city || state != o.state || pincode != o.pincode

        fun experienceDiffers(o: Snapshot) =
            yearsOfExperience != o.yearsOfExperience || about != o.about || languageIds != o.languageIds

        fun availabilityDiffers(o: Snapshot) =
            workingDays != o.workingDays || startTime != o.startTime || endTime != o.endTime

        fun addressError(): String? = when {
            houseNo.isBlank() -> "Enter your house number."
            city.isBlank() -> "Enter your city."
            state.isBlank() -> "Enter your state."
            pincode.length != 6 -> "Pincode must be 6 digits."
            else -> null
        }

        fun experienceError(): String? =
            if (languageIds.isEmpty()) "Select at least one language." else null

        fun availabilityError(): String? = when {
            workingDays.isEmpty() -> "Select at least one working day."
            startTime.isBlank() || endTime.isBlank() -> "Pick a start and end time."
            endTime <= startTime -> "End time must be after start time."   // "HH:mm:ss" compares correctly
            else -> null
        }
    }

    private var saved by mutableStateOf<Snapshot?>(null)

    private fun currentSnapshot() = Snapshot(
        houseNo = houseNo.trim(),
        city = city.trim(),
        state = state.trim(),
        pincode = pincode.trim(),
        yearsOfExperience = yearsOfExperience,
        about = about.trim(),
        languageIds = selectedLanguageIds,
        workingDays = workingDays,
        startTime = startTime,
        endTime = endTime
    )

    /** True once any field differs from what the server has. */
    val hasChanges: Boolean
        get() = saved?.let { it != currentSnapshot() } ?: false

    /** Why Save is blocked, checking only the sections that were changed. Null = OK. */
    val validationError: String?
        get() {
            val base = saved ?: return null
            val now = currentSnapshot()
            return (if (now.addressDiffers(base)) now.addressError() else null)
                ?: (if (now.experienceDiffers(base)) now.experienceError() else null)
                ?: (if (now.availabilityDiffers(base)) now.availabilityError() else null)
        }

    val isFormValid: Boolean
        get() = validationError == null

    // ── Loading the existing values ──────────────────────────────────────────

    /** Fills the form from the saved profile. Safe to call repeatedly; only the first call counts. */
    fun prefill(profile: HelperProfileResponse) {
        if (isReady) return

        houseNo = profile.house_no.orEmpty()
        city = profile.city.orEmpty()
        state = profile.state.orEmpty()
        pincode = profile.pincode.orEmpty().filter { it.isDigit() }.take(6)
        latitude = profile.latitude.orEmpty()
        longitude = profile.longitude.orEmpty()

        yearsOfExperience = experienceBucketFor(profile.years_of_experience)
        about = profile.about.orEmpty()
        selectedLanguageIds = profile.languages_spoken.orEmpty().map { it.id }.toSet()

        workingDays = profile.working_days.orEmpty().map { it.lowercase() }.toSet()
        startTime = profile.start_time.orEmpty()
        endTime = profile.end_time.orEmpty()

        saved = currentSnapshot()
        isReady = true
        loadLanguages()
    }

    fun loadLanguages() {
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

    // ── Field edits ──────────────────────────────────────────────────────────

    fun onHouseNoChange(v: String) { houseNo = v }
    fun onCityChange(v: String) { city = v }
    fun onStateChange(v: String) { state = v }
    fun onPincodeChange(v: String) { pincode = v.filter { it.isDigit() }.take(6) }

    fun onExperienceSelected(years: Int) { yearsOfExperience = years }
    fun onAboutChange(v: String) { about = v }

    fun toggleLanguage(id: Int) {
        selectedLanguageIds =
            if (id in selectedLanguageIds) selectedLanguageIds - id else selectedLanguageIds + id
    }

    fun toggleDay(code: String) {
        workingDays = if (code in workingDays) workingDays - code else workingDays + code
    }

    fun onStartTimePicked(v: String) { startTime = v }
    fun onEndTimePicked(v: String) { endTime = v }

    // ── Saving ───────────────────────────────────────────────────────────────

    fun save(onSuccess: () -> Unit) {
        val base = saved ?: return
        if (isSaving || !isFormValid) return

        viewModelScope.launch {
            isSaving = true
            errorMessage = null

            val now = currentSnapshot()
            var current = base

            if (now.addressDiffers(current)) {
                when (val result = repository.submitAddress(
                    houseNo = now.houseNo,
                    state = now.state,
                    city = now.city,
                    pincode = now.pincode,
                    latitude = latitude,
                    longitude = longitude
                )) {
                    is ApiResult.Success -> {
                        current = current.copy(
                            houseNo = now.houseNo, city = now.city,
                            state = now.state, pincode = now.pincode
                        )
                        saved = current
                    }
                    is ApiResult.Error -> {
                        errorMessage = "Address: ${result.message}"
                        isSaving = false
                        return@launch
                    }
                }
            }

            if (now.experienceDiffers(current)) {
                when (val result = repository.submitExperience(
                    yearsOfExperience = now.yearsOfExperience,
                    languagesSpoken = now.languageIds.sorted(),
                    about = now.about
                )) {
                    is ApiResult.Success -> {
                        current = current.copy(
                            yearsOfExperience = now.yearsOfExperience,
                            about = now.about,
                            languageIds = now.languageIds
                        )
                        saved = current
                    }
                    is ApiResult.Error -> {
                        errorMessage = "Experience: ${result.message}"
                        isSaving = false
                        return@launch
                    }
                }
            }

            if (now.availabilityDiffers(current)) {
                when (val result = repository.submitAvailability(
                    workingDays = DAY_CODES.filter { it in now.workingDays },
                    startTime = now.startTime,
                    endTime = now.endTime
                )) {
                    is ApiResult.Success -> {
                        current = current.copy(
                            workingDays = now.workingDays,
                            startTime = now.startTime,
                            endTime = now.endTime
                        )
                        saved = current
                    }
                    is ApiResult.Error -> {
                        errorMessage = "Availability: ${result.message}"
                        isSaving = false
                        return@launch
                    }
                }
            }

            isSaving = false
            onSuccess()
        }
    }

    companion object {
        /** Day codes in week order, as the backend stores them. */
        val DAY_CODES = listOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")

        /** The experience form stores the lower bound of each bucket. */
        val EXPERIENCE_OPTIONS = listOf(
            "Less than 1 year" to 0,
            "1-2 years" to 1,
            "3-5 years" to 3,
            "5+ years" to 5
        )

        fun experienceBucketFor(years: Int?): Int = when {
            years == null || years < 1 -> 0
            years < 3 -> 1
            years < 5 -> 3
            else -> 5
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(UserDetailsRepository(context.applicationContext)) as T
        }
    }
}