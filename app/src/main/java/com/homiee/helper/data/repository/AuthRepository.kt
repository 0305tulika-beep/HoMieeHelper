package com.homiee.helper.data.repository

import android.content.Context
import android.util.Log
import com.homiee.helper.data.local.TokenManager
import com.homiee.helper.data.model.ApiErrorResponse
import com.homiee.helper.data.model.AuthResponse
import com.homiee.helper.data.model.LoginRequest
import com.homiee.helper.data.model.OtpSentResponse
import com.homiee.helper.data.model.RegisterRequest
import com.homiee.helper.data.model.ResendOtpRequest
import com.homiee.helper.data.model.VerifyOtpRequest
import com.homiee.helper.data.remote.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
}

class AuthRepository(context: Context) {

    private val api = RetrofitClient.authApi
    private val tokenManager = TokenManager.getInstance(context)

    private fun <T> parseError(response: Response<T>): ApiResult.Error {
        val raw = response.errorBody()?.string()
        Log.e("AuthRepository", "HTTP ${response.code()} error body: $raw")

        if (raw.isNullOrBlank()) {
            return ApiResult.Error("Something went wrong (${response.code()})", response.code())
        }

        val wrapped = try {
            Gson().fromJson(raw, ApiErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
        wrapped?.errors?.values?.firstOrNull()?.firstOrNull()?.let {
            return ApiResult.Error(it, response.code())
        }
        if (!wrapped?.message.isNullOrBlank()) {
            return ApiResult.Error(wrapped!!.message!!, response.code())
        }

        val bareDetail = try {
            val type = object : TypeToken<Map<String, List<String>>>() {}.type
            val map: Map<String, List<String>> = Gson().fromJson(raw, type)
            map.entries.firstOrNull()?.let { (field, details) ->
                val detail = details.firstOrNull()
                if (detail != null) "$field: $detail" else null
            }
        } catch (e: Exception) {
            null
        }

        return ApiResult.Error(
            bareDetail ?: "Something went wrong (${response.code()})",
            response.code()
        )
    }

    suspend fun login(identifier: String, password: String): ApiResult<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(identifier, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.data?.let { data ->
                    tokenManager.saveTokens(data.tokens.access, data.tokens.refresh)
                    tokenManager.saveUser(
                        data.user.id, data.user.fname, data.user.lname,
                        data.user.email, data.user.username
                    )
                }
                ApiResult.Success(body)
            } else {
                parseError(response)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        username: String,
        password: String,
        password2: String
    ): ApiResult<OtpSentResponse> {
        return try {
            val response = api.register(
                RegisterRequest(
                    fname = firstName,
                    lname = lastName,
                    email = email,
                    username = username,
                    password = password,
                    password2 = password2
                )
            )
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                parseError(response)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
    }

    suspend fun resendOtp(identifier: String): ApiResult<OtpSentResponse> {
        return try {
            val response = api.resendOtp(ResendOtpRequest(identifier))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                parseError(response)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
    }

    suspend fun verifyOtp(identifier: String, otp: String): ApiResult<AuthResponse> {
        return try {
            val response = api.verifyOtp(VerifyOtpRequest(identifier, otp))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.data?.let { data ->
                    tokenManager.saveTokens(data.tokens.access, data.tokens.refresh)
                    tokenManager.saveUser(
                        data.user.id, data.user.fname, data.user.lname,
                        data.user.email, data.user.username
                    )
                }
                ApiResult.Success(body)
            } else {
                parseError(response)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
    }
}