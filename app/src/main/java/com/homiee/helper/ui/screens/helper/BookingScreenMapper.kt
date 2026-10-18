package com.homiee.helper.ui.screens.helper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import com.homiee.helper.data.model.BookingDto
import com.homiee.helper.data.model.amountLabel
import com.homiee.helper.data.model.dateLabel
import com.homiee.helper.data.model.endMillis
import com.homiee.helper.data.model.formatApiTime
import com.homiee.helper.data.model.startMillis
import com.homiee.helper.data.model.timeRangeLabel

/**
 * Converts API bookings into the JobRequest / JobBooking models your existing
 * screens already render, so the card layouts don't need to change.
 *
 * The bookings API does NOT return the resident's address, email or phone,
 * so location / fullAddress / email / phone stay blank. The screens already
 * hide blank values.
 */

fun serviceTypeFor(name: String): ServiceType {
    val n = name.lowercase()
    return when {
        "clean" in n -> ServiceType.Cleaning
        "cook" in n -> ServiceType.Cooking
        "baby" in n || "child" in n -> ServiceType.Babysitting
        "elder" in n -> ServiceType.Eldercare
        else -> ServiceType(name.ifBlank { "Service" }, Icons.Filled.Person)
    }
}

private fun initialsOf(name: String): String =
    name.trim().split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }

private fun minutesOf(time: String): Int? {
    val parts = time.split(":")
    val hours = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hours * 60 + minutes
}

private fun durationLabel(start: String, end: String): String {
    val s = minutesOf(start)
    val e = minutesOf(end)
    if (s == null || e == null || e <= s) return ""
    val total = e - s
    val h = total / 60
    val m = total % 60
    return when {
        m == 0 && h == 1 -> "1 Hour"
        m == 0 -> "$h Hours"
        h == 0 -> "$m Min"
        else -> "${h}h ${m}m"
    }
}

fun BookingDto.toJobRequest(): JobRequest = JobRequest(
    id = id.toString(),
    residentName = residentName.ifBlank { "Resident" },
    residentInitials = initialsOf(residentName),
    service = serviceTypeFor(service),
    location = "",
    fullAddress = "",
    date = dateLabel(),
    time = formatApiTime(startTime),
    duration = durationLabel(startTime, endTime),
    salary = amountLabel(),
    timeAgo = "",
    status = when (status.lowercase()) {
        "pending" -> RequestStatus.NEW
        "confirmed", "completed" -> RequestStatus.ACCEPTED
        else -> RequestStatus.REJECTED          // rejected / cancelled
    },
    instructions = specialInstructions.orEmpty()
)

/**
 * The status follows the clock for accepted jobs:
 *  - completed on the server, OR end time passed  -> COMPLETED
 *  - start time reached, end not yet              -> ACTIVE
 *  - otherwise                                    -> UPCOMING
 */
fun BookingDto.toJobBooking(nowMillis: Long = System.currentTimeMillis()): JobBooking {
    val confirmed = status.equals("confirmed", true)
    return JobBooking(
        id = id.toString(),
        residentName = residentName.ifBlank { "Resident" },
        residentInitials = initialsOf(residentName),
        service = serviceTypeFor(service),
        location = "",
        fullAddress = "",
        date = dateLabel(),
        time = timeRangeLabel(),
        duration = durationLabel(startTime, endTime),
        salary = amountLabel(),
        status = when {
            status.equals("completed", true) ||
                    (confirmed && endMillis() in 1..nowMillis) -> BookingStatus.COMPLETED
            confirmed && startMillis() in 1..nowMillis -> BookingStatus.ACTIVE
            else -> BookingStatus.UPCOMING
        },
        instructions = specialInstructions.orEmpty()
    )
}