package com.homiee.helper.data.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

// ── Shared booking object (incoming list + respond response) ──────────────────
data class BookingDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("resident_id") val residentId: Int = 0,
    @SerializedName("resident_name") val residentName: String = "",
    @SerializedName("helper_id") val helperId: Int = 0,
    @SerializedName("helper_name") val helperName: String = "",
    @SerializedName("service_id") val serviceId: Int = 0,
    @SerializedName("service") val service: String = "",
    @SerializedName("booking_date") val bookingDate: String = "",      // yyyy-MM-dd
    @SerializedName("start_time") val startTime: String = "",          // HH:mm:ss
    @SerializedName("end_time") val endTime: String = "",              // HH:mm:ss
    @SerializedName("special_instructions") val specialInstructions: String? = null,
    @SerializedName("total_amount") val totalAmount: String = "",      // e.g. "300.00"
    @SerializedName("status") val status: String = "",                 // pending | confirmed | completed | rejected | cancelled
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
)

// ── POST /api/bookings/bookings/{id}/respond/ ─────────────────────────────────
data class RespondRequest(
    val action: String   // "accept" | "reject"
)

// ── Responses ─────────────────────────────────────────────────────────────────
// respond
data class BookingResponse(
    val status: String? = null,
    val message: String? = null,
    val data: BookingDto? = null
)

// GET /api/bookings/bookings/incoming/
data class BookingListResponse(
    val status: String? = null,
    val message: String? = null,
    val data: List<BookingDto>? = null
)

// 400 / 401 / 403 / 404 body
data class BookingErrorResponse(
    val status: String? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)

// ── Display helpers (use these in the UI) ─────────────────────────────────────

/** "10:00:00" -> "10:00 AM" */
fun formatApiTime(raw: String): String {
    val parts = raw.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: return raw
    val minute = parts.getOrNull(1) ?: "00"
    val suffix = if (hour >= 12) "PM" else "AM"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$hour12:$minute $suffix"
}

/** "2026-06-12" -> "12 Jun 2026" */
fun formatApiDate(raw: String): String = try {
    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(raw)
    if (parsed == null) raw else SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsed)
} catch (e: Exception) {
    raw
}

fun BookingDto.dateLabel(): String = formatApiDate(bookingDate)

fun BookingDto.timeRangeLabel(): String =
    "${formatApiTime(startTime)} – ${formatApiTime(endTime)}"

fun BookingDto.amountLabel(): String =
    totalAmount.takeIf { it.isNotBlank() }?.let { "₹$it" } ?: ""