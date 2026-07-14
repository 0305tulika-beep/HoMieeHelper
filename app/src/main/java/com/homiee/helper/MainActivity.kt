package com.homiee.helper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.homiee.helper.ui.navigation.HomieeNavHost
import com.homiee.helper.ui.theme.HomieeHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomieeHelperTheme {
                HomieeNavHost()
            }
        }
    }
}