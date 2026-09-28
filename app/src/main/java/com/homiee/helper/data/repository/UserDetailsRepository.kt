package com.homiee.helper.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.homiee.helper.data.model.AddressRequest
import com.homiee.helper.data.model.AddressResponse
import com.homiee.helper.data.model.ApiErrorResponse
import com.homiee.helper.data.model.AvailabilityRequest
import com.homiee.helper.data.model.AvailabilityResponse
import com.homiee.helper.data.model.DocumentsResponse
import com.homiee.helper.data.model.ExperienceRequest
import com.homiee.helper.data.model.ExperienceResponse
import com.homiee.helper.data.model.IdentityResponse
import com.homiee.helper.data.model.LanguageDto
import com.homiee.helper.data.model.ServiceDto
import com.homiee.helper.data.model.ServicePriceItem
import com.homiee.helper.data.model.ServicesRequest
import com.homiee.helper.data.model.ServicesResponse
import com.homiee.helper.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

// Reuses the ApiResult sealed class already declared in AuthRepository.kt (same package).
class UserDetailsRepository(private val context: Context) {

    private val api = RetrofitClient.userDetailsApi

    private fun <T> parseError(response: Response<T>): ApiResult.Error {
        val raw = response.errorBody()?.string()
        Log.e("UserDetailsRepository", "HTTP ${response.code()} error body: $raw")

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
        return ApiResult.Error("Something went wrong (${response.code()})", response.code())
    }

    private fun textPart(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaTypeOrNull())

    /** Reads a picked content Uri fully into memory and wraps it as a multipart file part. */
    private fun filePart(partName: String, uri: Uri?): MultipartBody.Part? {
        if (uri == null) return null
        val resolver = context.contentResolver
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val mimeType = resolver.getType(uri) ?: "application/octet-stream"
        val extension = mimeType.substringAfterLast('/', missingDelimiterValue = "bin")
        val fileName = "${partName}_${System.currentTimeMillis()}.$extension"
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, body)
    }

    /** Flattens [ {...} ], [ [ {...} ] ] and { "results": [ {...} ] } into one list. */
    private fun <T> flattenList(root: JsonElement, clazz: Class<T>): List<T> {
        val gson = Gson()
        val out = mutableListOf<T>()
        fun walk(el: JsonElement) {
            when {
                el.isJsonArray -> el.asJsonArray.forEach { walk(it) }
                el.isJsonObject && el.asJsonObject.has("results") ->
                    walk(el.asJsonObject.get("results"))
                el.isJsonObject -> out.add(gson.fromJson(el, clazz))
            }
        }
        walk(root)
        return out
    }

    suspend fun submitIdentity(
        fullName: String,
        dateOfBirth: String,
        govtIdType: String,
        govtIdNumber: String,
        frontCardUri: Uri?,
        backCardUri: Uri?
    ): ApiResult<IdentityResponse> {
        return try {
            val response = api.submitIdentity(
                fullName = textPart(fullName),
                dateOfBirth = textPart(dateOfBirth),
                govtIdType = textPart(govtIdType),
                govtIdNumber = textPart(govtIdNumber),
                // Always false from the app - actual verification is a backend/admin action,
                // never something the onboarding form itself can set.
                idVerified = textPart("false"),
                frontCard = filePart("front_card", frontCardUri),
                backCard = filePart("back_card", backCardUri)
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

    suspend fun submitAddress(
        houseNo: String,
        state: String,
        city: String,
        pincode: String,
        latitude: String,
        longitude: String
    ): ApiResult<AddressResponse> {
        return try {
            val response = api.submitAddress(
                AddressRequest(
                    house_no = houseNo,
                    state = state,
                    city = city,
                    pincode = pincode,
                    latitude = latitude,
                    longitude = longitude
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

    suspend fun submitDocuments(
        profilePhotoUri: Uri?,
        policeCertUri: Uri?
    ): ApiResult<DocumentsResponse> {
        return try {
            val response = api.submitDocuments(
                filePart("profile_photo", profilePhotoUri),
                filePart("police_verification_cert", policeCertUri)
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

    /** Fetches the real service catalog (ids + names) from the backend. */
    suspend fun getServices(): ApiResult<List<ServiceDto>> {
        return try {
            val response = api.getServices()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiResult.Success(flattenList(body, ServiceDto::class.java))
            } else {
                parseError(response)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
    }

    /** Fetches the real language catalog (ids + names) from the backend. */
    suspend fun getLanguages(): ApiResult<List<LanguageDto>> {
        return try {
            val response = api.getLanguages()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiResult.Success(flattenList(body, LanguageDto::class.java))
            } else {
                parseError(response)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
    }

    suspend fun submitServices(servicePrices: List<ServicePriceItem>): ApiResult<ServicesResponse> {
        return try {
            val response = api.submitServices(ServicesRequest(servicePrices))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                parseError(response)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
    }

    suspend fun submitExperience(
        yearsOfExperience: Int,
        languagesSpoken: List<Int>,
        about: String
    ): ApiResult<ExperienceResponse> {
        return try {
            val response = api.submitExperience(
                ExperienceRequest(yearsOfExperience, languagesSpoken, about)
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

    suspend fun submitAvailability(
        workingDays: List<String>,
        startTime: String,
        endTime: String
    ): ApiResult<AvailabilityResponse> {
        return try {
            val response = api.submitAvailability(
                AvailabilityRequest(workingDays, startTime, endTime)
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
}