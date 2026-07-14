package com.homiee.helper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homiee.helper.ui.theme.BackgroundWhite
import com.homiee.helper.ui.theme.TextPrimary

/**
 * Joint shared scaffold used by the Sign Up, Login, OTP, Forgot Password and
 * Reset Password screens.
 *
 * Responsibilities (shared across all five screens, in one place):
 *  1. Applies [TransparentSystemBars] - both status bar and navigation bar are
 *     made fully transparent so the app's background shows through them.
 *  2. Pads the content with `WindowInsets.systemBars` so the actual UI
 *     (title, fields, buttons) always sits strictly BETWEEN the two bars,
 *     never underneath them, even though the bars themselves are transparent.
 *  3. Provides the consistent back button + horizontal padding + optional
 *     scrolling used by every auth-flow screen.
 */
@Composable
fun AuthScaffold(
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    // Single source of truth for system bar styling across all auth screens.
    TransparentSystemBars(useDarkIcons = true)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Keeps content clear of both transparent bars.
                .windowInsetsPadding(WindowInsets.systemBars)
                .then(
                    if (scrollable) Modifier.verticalScroll(rememberScrollState())
                    else Modifier
                )
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            if (showBackButton) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(40.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}