package com.homiee.helper

import android.app.Application
import com.homiee.helper.data.remote.RetrofitClient

class HomieeHelperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}