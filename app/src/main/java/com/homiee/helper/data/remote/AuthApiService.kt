package com.homiee.helper.data.remote

import com.homiee.helper.data.model.AuthResponse
import com.homiee.helper.data.model.DeactivateRequest
import com.homiee.helper.data.model.GenericResponse
import com.homiee.helper.data.model.LoginRequest
import com.homiee.helper.data.model.OtpSentResponse
import com.homiee.helper.data.model.RegisterRequest
import com.homiee.helper.data.model.ResendOtpRequest
import com.homiee.helper.data.model.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<OtpSentResponse>

    @POST("api/auth/resend-otp/")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<OtpSentResponse>

    @POST("api/auth/verify-otp/")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @POST("api/auth/logout/")
    suspend fun logout(): Response<GenericResponse>

    @POST("api/auth/deactivate/")
    suspend fun deactivateAccount(@Body request: DeactivateRequest): Response<GenericResponse>

    // Plain @DELETE doesn't allow a request body in Retrofit — @HTTP with
    // hasBody = true is required to send one on a DELETE call.
    @HTTP(method = "DELETE", path = "api/auth/delete/", hasBody = true)
    suspend fun deleteAccount(@Body request: DeactivateRequest): Response<GenericResponse>
}