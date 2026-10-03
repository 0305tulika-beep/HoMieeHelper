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
import java.text.SimpleDateFormat
import java.util.Locale

class AvailabilityViewModel(
    private val repository: UserDetailsRepository
) : ViewModel() {

    // ---- Submit state ----
    var isLoading by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    // ---- Form state (lives here so it survives navigating away and back) ----
    // Display day names ("Mon", "Tue", ...). No days preselected.
    val selectedDays = mutableStateListOf<String>()
    var startTime by mutableStateOf(""); private set
    var endTime by mutableStateOf(""); private set

    private val startIndex: Int get() = timeSlots.indexOf(startTime) // -1 if not chosen
    private val endIndex: Int get() = timeSlots.indexOf(endTime)     // -1 if not chosen

    // Only offer times that keep start strictly before end.
    val startOptions: List<String>
        get() = if (endIndex >= 0) timeSlots.filterIndexed { i, _ -> i < endIndex } else timeSlots
    val endOptions: List<String>
        get() = if (startIndex >= 0) timeSlots.filterIndexed { i, _ -> i > startIndex } else timeSlots

    /** Continue stays disabled until days + a valid start/end are chosen. */
    val isFormValid: Boolean
        get() = selectedDays.isNotEmpty() &&
                startIndex >= 0 &&
                endIndex >= 0 &&
                startIndex < endIndex

    fun toggleDay(day: String) {
        if (!selectedDays.remove(day)) selectedDays.add(day)
    }

    fun onStartSelected(picked: String) {
        startTime = picked
        // If the chosen end is no longer after the new start, clear it.
        if (endIndex in 0..timeSlots.indexOf(picked)) endTime = ""
    }

    fun onEndSelected(picked: String) { endTime = picked }

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

    // API expects 24-hour "HH:mm:ss" (e.g. "09:00:00"); the dropdown shows
    // 12-hour labels, so convert on submit.
    private fun to24HourSeconds(label: String): String {
        return try {
            val input = SimpleDateFormat("h:mm a", Locale.US)
            val output = SimpleDateFormat("HH:mm:ss", Locale.US)
            output.format(input.parse(label)!!)
        } catch (e: Exception) {
            "00:00:00"
        }
    }

    companion object {
        // Display day -> backend day code (working_days: [String]).
        private val dayCodeMap = mapOf(
            "Mon" to "mon", "Tue" to "tue", "Wed" to "wed", "Thu" to "thu",
            "Fri" to "fri", "Sat" to "sat", "Sun" to "sun"
        )

        // Ordered earliest -> latest, so the list index can be used to compare times.
        private val timeSlots = listOf(
            "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
            "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"
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