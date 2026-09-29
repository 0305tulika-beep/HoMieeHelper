package com.homiee.helper.data.remote

import android.content.Context
import com.homiee.helper.data.local.SessionManager
import com.homiee.helper.data.local.TokenManager
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
                    path.contains("/api/auth/verify-otp")

            val token = tokenManager.getAccessToken()
            val request = if (!isPublicAuthEndpoint && !token.isNullOrBlank()) {
                original.newBuilder().addHeader("Authorization", "Bearer $token").build()
            } else original

            chain.proceed(request)
        }

        // Fail-safe only — not a real refresh-token exchange. On any 401 we
        // clear the local session and flip SessionManager.sessionExpired so
        // any screen watching it can bounce the user back to Login.
        val authenticator = Authenticator { _, _ ->
            tokenManager.clearSession()
            SessionManager.flagSessionExpired()
            null
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