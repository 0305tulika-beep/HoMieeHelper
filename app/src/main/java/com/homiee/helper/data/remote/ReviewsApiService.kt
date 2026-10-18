package com.homiee.helper.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Same endpoint the resident app uses: the helper detail response carries
// avg_rating, rating_count and up to 20 recent ratings.

data class RatingDto(
    val id: Int,
    val score: Int?,
    val feedback: String?,
    val created_at: String?
)

data class HelperRatingsData(
    val avg_rating: String?,
    val rating_count: Int?,
    val ratings: List<RatingDto>?
)

data class HelperRatingsResponse(
    val status: String?,
    val message: String?,
    val data: HelperRatingsData?
)

interface ReviewsApiService {
    @GET("api/bookings/helpers/{helper_id}/")
    suspend fun helperRatings(@Path("helper_id") helperId: Int): Response<HelperRatingsResponse>
}