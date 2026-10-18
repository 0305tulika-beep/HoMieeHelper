package com.homiee.helper.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

    // ---- Submit state ----
    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    // ---- Form state (lives here so it survives navigating away and back) ----
    // Display day names ("Mon", "Tue", ...). No days preselected.
    val selectedDays = mutableStateListOf<String>()

    // 24-hour "HH:mm" (e.g. "09:30"). Empty string = not chosen yet.
    var startTime by mutableStateOf(""); private set
    var endTime by mutableStateOf(""); private set

    /** "HH:mm" -> minutes since midnight, or -1 if empty/invalid. */
    private fun toMinutes(time: String): Int {
        val parts = time.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: return -1
        val m = parts.getOrNull(1)?.toIntOrNull() ?: return -1
        return h * 60 + m
    }

    private val startMinutes: Int get() = toMinutes(startTime)
    private val endMinutes: Int get() = toMinutes(endTime)

    /** Continue stays disabled until days + a valid start/end are chosen. */
    val isFormValid: Boolean
        get() = selectedDays.isNotEmpty() &&
                startMinutes >= 0 &&
                endMinutes >= 0 &&
                startMinutes < endMinutes

    fun toggleDay(day: String) {
        if (!selectedDays.remove(day)) selectedDays.add(day)
    }

    fun onStartSelected(picked: String) {
        startTime = picked
        errorMessage = null
        // If the chosen end is no longer after the new start, clear it.
        if (endMinutes in 0..toMinutes(picked)) {
            endTime = ""
        }
    }

    fun onEndSelected(picked: String) {
        if (startMinutes >= 0 && toMinutes(picked) <= startMinutes) {
            // Reject an end time that is not after the start time.
            errorMessage = "End time must be after start time"
            return
        }
        endTime = picked
        errorMessage = null
    }

    fun submit(onSuccess: () -> Unit) {
        if (!isFormValid || isLoading) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = repository.submitAvailability(
                selectedDays.mapNotNull { dayCodeMap[it] },
                to24HourSeconds(startTime),
                to24HourSeconds(endTime)
            )) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.Error -> errorMessage = result.message
            }
            isLoading = false
        }
    }

    // API expects 24-hour "HH:mm:ss" (e.g. "09:00:00"). We already store "HH:mm",
    // so just append the seconds.
    private fun to24HourSeconds(time: String): String {
        val minutes = toMinutes(time)
        if (minutes < 0) return "00:00:00"
        return String.format(java.util.Locale.US, "%02d:%02d:00", minutes / 60, minutes % 60)
    }

    companion object {
        // Display day -> backend day code (working_days: [String]).
        private val dayCodeMap = mapOf(
            "Mon" to "mon", "Tue" to "tue", "Wed" to "wed", "Thu" to "thu",
            "Fri" to "fri", "Sat" to "sat", "Sun" to "sun"
        )

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