package com.homiee.helper.data.model

// ── Requests ──────────────────────────────────────────────────────────────

data class AddressRequest(
    val house_no: String,
    val state: String,
    val city: String,
    val pincode: String,
    val latitude: String,
    val longitude: String
)

// ── Responses ─────────────────────────────────────────────────────────────
// Success bodies for these three endpoints come back as the raw saved object
// (no status/message/data wrapper) - error bodies still use ApiErrorResponse.

data class IdentityResponse(
    val full_name: String? = null,
    val date_of_birth: String? = null,
    val govt_id_type: String? = null,
    val govt_id_number: String? = null,
    val front_card: String? = null,
    val back_card: String? = null,
    val id_verified: Boolean? = null
)

data class AddressResponse(
    val house_no: String? = null,
    val state: String? = null,
    val city: String? = null,
    val pincode: String? = null,
    val latitude: String? = null,
    val longitude: String? = null
)

data class DocumentsResponse(
    val profile_photo: String? = null,
    val police_verification_cert: String? = null
)

// ── Services catalog (GET /api/userdetails/userdetails/services/) ───────────
data class ServiceDto(
    val id: Int = 0,
    val name: String = "",
    val slug: String? = null
)

// ── Languages catalog ───────────────────────────────────────────────────────
data class LanguageDto(
    val id: Int = 0,
    val name: String = "",
    val code: String? = null
)

// ── Services & Pricing (step 4) ─────────────────────────────────────────────
data class ServicePriceItem(
    val service: Int,
    val price_per_hour: String
)

data class ServicesRequest(val service_prices: List<ServicePriceItem>)
data class ServicesResponse(val service_prices: List<ServicePriceItem>? = null)

// ── Experience & Languages (step 5) ─────────────────────────────────────────
data class ExperienceRequest(
    val years_of_experience: Int,
    val languages_spoken: List<Int>,
    val about: String
)

data class ExperienceResponse(
    val years_of_experience: Int? = null,
    val languages_spoken: List<Int>? = null,
    val about: String? = null
)

// ── Availability (step 6) ────────────────────────────────────────────────────
data class AvailabilityRequest(
    val working_days: List<String>,
    val start_time: String,
    val end_time: String
)

data class AvailabilityResponse(
    val working_days: List<String>? = null,
    val start_time: String? = null,
    val end_time: String? = null
)