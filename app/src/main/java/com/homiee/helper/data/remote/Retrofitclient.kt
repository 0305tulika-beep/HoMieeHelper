package com.homiee.helper.data.remote

import android.content.Context
import com.google.gson.Gson
import com.homiee.helper.data.local.SessionManager
import com.homiee.helper.data.local.TokenManager
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://13.206.80.56/"

    @Volatile private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /** Turns a backend path like "/media/helper_photos/x.jpg" into a full URL. */
    fun absoluteUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        return BASE_URL.trimEnd('/') + "/" + path.trimStart('/')
    }

    // ---- Token refresh ------------------------------------------------------------

    // POST /api/auth/refresh/  body: {"refresh": "..."}  ->  {"access": "...", "refresh": "..."}
    private data class RefreshResponse(val access: String?, val refresh: String?)

    private sealed class RefreshResult {
        data class Success(val access: String, val refresh: String) : RefreshResult()
        /** Server said the refresh token is invalid/expired -> the session is really over. */
        object Rejected : RefreshResult()
        /** Network error / server error -> NOT a reason to log the user out. */
        object Failed : RefreshResult()
    }

    private val refreshLock = Any()

    // Plain client (no interceptor, no authenticator) so refreshing can never loop on itself.
    private val refreshClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private fun refreshTokens(refreshToken: String): RefreshResult {
        return try {
            val body = JSONObject().put("refresh", refreshToken).toString()
                .toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(BASE_URL + "api/auth/refresh/")
                .post(body)
                .build()

            refreshClient.newCall(request).execute().use { resp ->
                when {
                    resp.isSuccessful -> {
                        val parsed = Gson().fromJson(resp.body?.string(), RefreshResponse::class.java)
                        val access = parsed?.access
                        if (access.isNullOrBlank()) {
                            RefreshResult.Failed
                        } else {
                            // If rotation is off the server may omit "refresh" - keep the old one.
                            val newRefresh = parsed.refresh?.takeIf { it.isNotBlank() } ?: refreshToken
                            RefreshResult.Success(access, newRefresh)
                        }
                    }
                    resp.code == 400 || resp.code == 401 || resp.code == 403 -> RefreshResult.Rejected
                    else -> RefreshResult.Failed
                }
            }
        } catch (e: Exception) {
            RefreshResult.Failed
        }
    }

    private fun attemptCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    // ---- Retrofit -----------------------------------------------------------------

    private val retrofit: Retrofit by lazy {
        val context = requireNotNull(appContext) {
            "RetrofitClient.init(context) must be called before use — call it from " +
                    "your Application class's onCreate()."
        }
        val tokenManager = TokenManager.getInstance(context)

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val path = original.url.encodedPath
            val isPublicAuthEndpoint = path.contains("/api/auth/login") ||
                    path.contains("/api/auth/register") ||
                    path.contains("/api/auth/resend-otp") ||
                    path.contains("/api/auth/verify-otp") ||
                    path.contains("/api/auth/refresh")

            val token = tokenManager.getAccessToken()
            val request = if (!isPublicAuthEndpoint && !token.isNullOrBlank()) {
                original.newBuilder().addHeader("Authorization", "Bearer $token").build()
            } else original

            chain.proceed(request)
        }

        fun expireSession() {
            tokenManager.clearSession()
            SessionManager.flagSessionExpired()
        }

        // On a 401: exchange the refresh token for a new access token and retry the request.
        // The user is logged out ONLY if the server rejects the refresh token itself.
        val authenticator = Authenticator { _, response ->
            val sentAuth = response.request.header("Authorization")
            val path = response.request.url.encodedPath

            // No token was sent (public endpoint) -> nothing to refresh.
            if (sentAuth == null || path.contains("/api/auth/refresh")) return@Authenticator null
            // Already retried with a fresh token and still 401 -> give up on this request.
            if (attemptCount(response) >= 2) return@Authenticator null

            synchronized(refreshLock) {
                // Another request may have refreshed while we waited for the lock.
                val currentAccess = tokenManager.getAccessToken()
                if (!currentAccess.isNullOrBlank() && "Bearer $currentAccess" != sentAuth) {
                    return@Authenticator response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccess")
                        .build()
                }

                val refresh = tokenManager.getRefreshToken()
                if (refresh.isNullOrBlank()) {
                    expireSession()
                    return@Authenticator null
                }

                when (val result = refreshTokens(refresh)) {
                    is RefreshResult.Success -> {
                        tokenManager.saveTokens(result.access, result.refresh)
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${result.access}")
                            .build()
                    }
                    RefreshResult.Rejected -> {
                        expireSession()
                        null
                    }
                    RefreshResult.Failed -> null // keep the session; the user can just retry
                }
            }
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .authenticator(authenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val userDetailsApi: UserDetailsApiService by lazy { retrofit.create(UserDetailsApiService::class.java) }
}