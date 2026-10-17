package com.homiee.helper.data.repository

import android.util.Log
import com.google.gson.Gson
import com.homiee.helper.data.model.BookingErrorResponse
import com.homiee.helper.data.model.BookingListResponse
import com.homiee.helper.data.model.BookingResponse
import com.homiee.helper.data.model.RespondRequest
import com.homiee.helper.data.remote.RetrofitClient
import retrofit2.Response

// Named BookingResult so it can't clash with any result class you already have
// (e.g. in AuthRepository). If you already have a shared Success/Error wrapper,
// swap this one out for it.
sealed class BookingResult<out T> {
    data class Success<T>(val data: T) : BookingResult<T>()
    data class Error(val message: String) : BookingResult<Nothing>()
}

class BookingRepository {

    private val api by lazy { RetrofitClient.bookingApi }

    suspend fun incomingBookings(status: String? = null): BookingResult<BookingListResponse> =
        safeApiCall { api.incomingBookings(status) }

    suspend fun respond(id: Int, action: String): BookingResult<BookingResponse> =
        safeApiCall { api.respondToBooking(id, RespondRequest(action)) }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): BookingResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) BookingResult.Success(body)
                else BookingResult.Error("Something went wrong. Please try again.")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("BOOKING_API_ERROR", "HTTP ${response.code()} - Body: ${errorBody?.take(300)}")
                BookingResult.Error(parseErrorMessage(errorBody, response.code()))
            }
        } catch (e: Exception) {
            Log.e("BOOKING_API_ERROR", "Exception: ${e.javaClass.simpleName} - ${e.message}", e)
            BookingResult.Error("Unable to connect. Please check your internet connection.")
        }
    }

    private fun parseErrorMessage(errorBody: String?, code: Int): String {
        if (errorBody.isNullOrBlank()) return genericMessageFor(code)
        return try {
            val parsed = Gson().fromJson(errorBody, BookingErrorResponse::class.java)
            parsed?.errors?.values?.firstOrNull()?.firstOrNull()
                ?: parsed?.message
                ?: genericMessageFor(code)
        } catch (e: Exception) {
            genericMessageFor(code)
        }
    }

    private fun genericMessageFor(code: Int): String = when (code) {
        400 -> "Please check your details and try again."
        401 -> "Your session has expired. Please log in again."
        403 -> "You don't have access to this."
        404 -> "Booking not found."
        else -> "Something went wrong. Please try again."
    }
}