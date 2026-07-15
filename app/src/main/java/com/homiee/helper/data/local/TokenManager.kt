package com.homiee.helper.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager private constructor(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(access: String, refresh: String) {
        prefs.edit()
            .putString(KEY_ACCESS, access)
            .putString(KEY_REFRESH, refresh)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    fun saveUser(
        id: Int, fname: String, lname: String,
        email: String, username: String
    ) {
        prefs.edit()
            .putInt(KEY_USER_ID, id)
            .putString(KEY_FNAME, fname)
            .putString(KEY_LNAME, lname)
            .putString(KEY_EMAIL, email)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun getUserFullName(): String {
        val f = prefs.getString(KEY_FNAME, "") ?: ""
        val l = prefs.getString(KEY_LNAME, "") ?: ""
        return "$f $l".trim()
    }

    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun clearSession() {
        prefs.edit()
            .remove(KEY_ACCESS)
            .remove(KEY_REFRESH)
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "homiee_helper_secure_prefs"
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FNAME = "user_fname"
        private const val KEY_LNAME = "user_lname"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_USERNAME = "user_username"

        @Volatile private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
    }
}