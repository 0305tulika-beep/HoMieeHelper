package com.homiee.helper.data.remote

import com.homiee.helper.data.model.BookingListResponse
import com.homiee.helper.data.model.BookingResponse
import com.homiee.helper.data.model.RespondRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApiService {

    // Bookings assigned to the logged-in helper.
    // status = null -> backend defaults to "pending"; status = "all" -> every status.
    @GET("api/bookings/bookings/incoming/")
    suspend fun incomingBookings(
        @Query("status") status: String? = null
    ): Response<BookingListResponse>

    // Helper accepts or rejects a pending request. Body: {"action": "accept" | "reject"}
    @POST("api/bookings/bookings/{id}/respond/")
    suspend fun respondToBooking(
        @Path("id") id: Int,
        @Body request: RespondRequest
    ): Response<BookingResponse>
}