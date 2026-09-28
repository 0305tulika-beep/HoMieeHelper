package com.homiee.helper.data.remote

import com.google.gson.JsonElement
import com.homiee.helper.data.model.AddressRequest
import com.homiee.helper.data.model.AddressResponse
import com.homiee.helper.data.model.AvailabilityRequest
import com.homiee.helper.data.model.AvailabilityResponse
import com.homiee.helper.data.model.DocumentsResponse
import com.homiee.helper.data.model.ExperienceRequest
import com.homiee.helper.data.model.ExperienceResponse
import com.homiee.helper.data.model.IdentityResponse
import com.homiee.helper.data.model.ServicesRequest
import com.homiee.helper.data.model.ServicesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserDetailsApiService {

    // Step 1 - identity verification
    @Multipart
    @POST("api/userdetails/helpers/identity/")
    suspend fun submitIdentity(
        @Part("full_name") fullName: RequestBody,
        @Part("date_of_birth") dateOfBirth: RequestBody,
        @Part("govt_id_type") govtIdType: RequestBody,
        @Part("govt_id_number") govtIdNumber: RequestBody,
        @Part("id_verified") idVerified: RequestBody,
        @Part frontCard: MultipartBody.Part?,
        @Part backCard: MultipartBody.Part?
    ): Response<IdentityResponse>

    // Step 2 - address
    @POST("api/userdetails/helpers/address/")
    suspend fun submitAddress(@Body request: AddressRequest): Response<AddressResponse>

    // Step 3 - profile photo & documents
    @Multipart
    @POST("api/userdetails/helpers/documents/")
    suspend fun submitDocuments(
        @Part profilePhoto: MultipartBody.Part?,
        @Part policeVerificationCert: MultipartBody.Part?
    ): Response<DocumentsResponse>

    // Catalog of valid services (real primary keys used by step 4)
    @GET("api/userdetails/userdetails/services/")
    suspend fun getServices(): Response<JsonElement>

    // Step 4 - services & pricing
    @POST("api/userdetails/helpers/services/")
    suspend fun submitServices(@Body request: ServicesRequest): Response<ServicesResponse>

    // Catalog of valid languages (real primary keys used by step 5)
    // NOTE: path is assumed to mirror the services catalog - verify in Swagger.
    @GET("api/userdetails/userdetails/languages/")
    suspend fun getLanguages(): Response<JsonElement>

    // Step 5 - experience & languages
    @POST("api/userdetails/helpers/experience/")
    suspend fun submitExperience(@Body request: ExperienceRequest): Response<ExperienceResponse>

    // Step 6 - availability
    @POST("api/userdetails/helpers/availability/")
    suspend fun submitAvailability(@Body request: AvailabilityRequest): Response<AvailabilityResponse>
}