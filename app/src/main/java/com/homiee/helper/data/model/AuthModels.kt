package com.homiee.helper.data.model

import com.google.gson.annotations.SerializedName

// ── Requests ──────────────────────────────────────────────────────────────

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class RegisterRequest(
    val fname: String,
    val lname: String,
    val email: String,
    val username: String,
    val role: String = "helper",
    val password: String,
    val password2: String
)

data class ResendOtpRequest(
    val identifier: String
)

data class VerifyOtpRequest(
    val identifier: String,
    val otp: String
)

data class DeactivateRequest(
    val password: String
)

// ── Shared pieces ─────────────────────────────────────────────────────────

data class UserDto(
    val id: Int,
    val fname: String,
    val lname: String,
    val email: String,
    val username: String
)

data class TokensDto(
    val access: String,
    val refresh: String
)

data class AuthDataDto(
    val user: UserDto,
    val tokens: TokensDto
)

// ── Responses ─────────────────────────────────────────────────────────────

// Used by /auth/login/ (200) and /auth/verify-otp/ (201)
data class AuthResponse(
    val status: String,
    val message: String,
    val data: AuthDataDto?
)

// Used by /auth/register/ (200) and /auth/resend-otp/ (200)
data class OtpSentData(
    val identifier: String,
    val channel: String? = null,
    @SerializedName("otp_expires_in") val otpExpiresIn: String? = null
)

data class OtpSentResponse(
    val status: String,
    val message: String,
    val data: OtpSentData?
)

// Used by /auth/logout/, /auth/deactivate/, /auth/delete/
data class GenericResponse(
    val status: String,
    val message: String
)

// Shape of 400/429/409 error bodies
data class ApiErrorResponse(
    val status: String? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)