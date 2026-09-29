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
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import com.homiee.helper.data.model.HelperProfileResponse

// Reuses the ApiResult sealed class already declared in AuthRepository.kt (same package).
class UserDetailsRepository(private val context: Context) {

    private val api = RetrofitClient.userDetailsApi
    private val gson = Gson()

    private companion object {
        const val TAG = "UserDetailsRepository"
    }

    // ── Shared plumbing ─────────────────────────────────────────────────────

    private fun <T> parseError(response: Response<T>): ApiResult.Error {
        val raw = response.errorBody()?.string()
        Log.e(TAG, "HTTP ${response.code()} error body: $raw")

        if (raw.isNullOrBlank()) {
            return ApiResult.Error("Something went wrong (${response.code()})", response.code())
        }
        val wrapped = try {
            gson.fromJson(raw, ApiErrorResponse::class.java)
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

    /** Runs a Retrofit call and maps it to ApiResult. Never swallows coroutine cancellation. */
    private suspend fun <T> call(block: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = block()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiResult.Success(body)
            } else {
                parseError(response)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Request failed", e)
            ApiResult.Error(e.message ?: "Network error. Please try again.")
        }
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

    /**
     * Flattens [ {...} ], [ [ {...} ] ], { "results": [...] } and { "data": [...] }
     * into one list. Objects without an "id" (e.g. a stray wrapper) are ignored.
     */
    private fun <T> flattenList(root: JsonElement, clazz: Class<T>): List<T> {
        val out = mutableListOf<T>()
        fun walk(el: JsonElement) {
            when {
                el.isJsonArray -> el.asJsonArray.forEach { walk(it) }
                el.isJsonObject && el.asJsonObject.has("results") ->
                    walk(el.asJsonObject.get("results"))
                el.isJsonObject && el.asJsonObject.has("data") ->
                    walk(el.asJsonObject.get("data"))
                el.isJsonObject && el.asJsonObject.has("id") ->
                    out.add(gson.fromJson(el, clazz))
            }
        }
        walk(root)
        return out
    }
    // ── Profile ─────────────────────────────────────────────────────────────

    suspend fun getProfile(): ApiResult<HelperProfileResponse> = call { api.getProfile() }


    // ── Steps 1-3 ───────────────────────────────────────────────────────────

    suspend fun submitIdentity(
        fullName: String,
        dateOfBirth: String,
        govtIdType: String,
        govtIdNumber: String,
        frontCardUri: Uri?,
        backCardUri: Uri?
    ): ApiResult<IdentityResponse> = call {
        api.submitIdentity(
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
    }

    suspend fun submitAddress(
        houseNo: String,
        state: String,
        city: String,
        pincode: String,
        latitude: String,
        longitude: String
    ): ApiResult<AddressResponse> = call {
        api.submitAddress(
            AddressRequest(
                house_no = houseNo,
                state = state,
                city = city,
                pincode = pincode,
                latitude = latitude,
                longitude = longitude
            )
        )
    }

    suspend fun submitDocuments(
        profilePhotoUri: Uri?,
        policeCertUri: Uri?
    ): ApiResult<DocumentsResponse> = call {
        api.submitDocuments(
            filePart("profile_photo", profilePhotoUri),
            filePart("police_verification_cert", policeCertUri)
        )
    }

    // ── Step 4 ──────────────────────────────────────────────────────────────

    /** Fetches the real service catalog (ids + names) from the backend. */
    suspend fun getServices(): ApiResult<List<ServiceDto>> =
        when (val result = call { api.getServices() }) {
            is ApiResult.Success -> {
                val list = flattenList(result.data, ServiceDto::class.java)
                    .filter { it.name.isNotBlank() }
                    .distinctBy { it.id }
                Log.d(TAG, "services parsed: ${list.size}")
                ApiResult.Success(list)
            }
            is ApiResult.Error -> result
        }

    suspend fun submitServices(servicePrices: List<ServicePriceItem>): ApiResult<ServicesResponse> =
        call { api.submitServices(ServicesRequest(servicePrices)) }

    // ── Step 5 ──────────────────────────────────────────────────────────────

    /** Fetches the real language catalog (ids + names + codes) from the backend. */
    suspend fun getLanguages(): ApiResult<List<LanguageDto>> =
        when (val result = call { api.getLanguages() }) {
            is ApiResult.Success -> {
                Log.d(TAG, "languages raw: ${result.data}")
                val list = flattenList(result.data, LanguageDto::class.java)
                    .filter { it.name.isNotBlank() }
                    .distinctBy { it.id }
                Log.d(TAG, "languages parsed: ${list.size}")
                ApiResult.Success(list)
            }
            is ApiResult.Error -> result
        }

    suspend fun submitExperience(
        yearsOfExperience: Int,
        languagesSpoken: List<Int>,
        about: String
    ): ApiResult<ExperienceResponse> =
        call { api.submitExperience(ExperienceRequest(yearsOfExperience, languagesSpoken, about)) }

    // ── Step 6 ──────────────────────────────────────────────────────────────

    suspend fun submitAvailability(
        workingDays: List<String>,
        startTime: String,
        endTime: String
    ): ApiResult<AvailabilityResponse> =
        call { api.submitAvailability(AvailabilityRequest(workingDays, startTime, endTime)) }
}