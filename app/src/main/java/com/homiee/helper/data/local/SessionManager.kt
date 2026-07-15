package com.homiee.helper.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {
    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired: StateFlow<Boolean> = _sessionExpired

    fun flagSessionExpired() {
        _sessionExpired.value = true
    }

    fun consumeSessionExpired() {
        _sessionExpired.value = false
    }
}