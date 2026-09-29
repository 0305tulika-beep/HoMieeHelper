package com.homiee.helper.data.model

// ── GET /api/userdetails/helpers/profile/ ─────────────────────────────────
// Raw object (no status/message/data wrapper). Reuses ServiceDto and LanguageDto
// from UserDetailsModels since the nested shapes are identical.

data class ServicePriceDetail(
    val service: ServiceDto? = null,
    val price_per_hour: String? = null
)

data class HelperProfileResponse(
    val full_name: String? = null,
    val date_of_birth: String? = null,
    val govt_id_type: String? = null,
    val govt_id_number: String? = null,
    val front_card: String? = null,
    val back_card: String? = null,
    val house_no: String? = null,
    val state: String? = null,
    val city: String? = null,
    val pincode: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val profile_photo: String? = null,
    val police_verification_cert: String? = null,
    val service_prices: List<ServicePriceDetail>? = null,
    val years_of_experience: Int? = null,
    val languages_spoken: List<LanguageDto>? = null,
    val about: String? = null,
    val working_days: List<String>? = null,
    val start_time: String? = null,
    val end_time: String? = null,
    val emergency_contact_name: String? = null,
    val emergency_contact_relation: String? = null,
    val emergency_contact_mobile: String? = null,
    val emergency_contact_verified: Boolean? = null
)