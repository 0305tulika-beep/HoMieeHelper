package com.homiee.helper.ui.screens.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.homiee.helper.ui.screens.auth.ForgotPasswordScreen
import com.homiee.helper.ui.screens.auth.LoginScreen
import com.homiee.helper.ui.screens.auth.OtpScreen
import com.homiee.helper.ui.screens.auth.ResetPasswordScreen
import com.homiee.helper.ui.screens.auth.SignUpScreen
import com.homiee.helper.ui.screens.auth.SplashScreen
import com.homiee.helper.ui.theme.HomieeHelperTheme

/**
 * Single consolidated preview file for the whole auth flow.
 *
 * All navigation/action callbacks are no-ops here since a preview has no
 * real NavController or backend - these exist purely so you can check
 * layout, spacing, and the transparent/hidden system-bars look while
 * iterating on UI, without running the app on a device or emulator.
 *
 * Note: [com.homiee.helper.ui.components.HiddenSystemBars] and
 * [com.homiee.helper.ui.components.TransparentSystemBars] both check
 * `LocalView.current.isInEditMode` and no-op inside the preview renderer,
 * so every screen below (including Splash) is safe to render here without
 * a real Activity/window.
 */

private const val PREVIEW_WIDTH_DP = 360
private const val PREVIEW_HEIGHT_DP = 780

@Preview(name = "Splash Screen", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun SplashScreenPreview() {
    HomieeHelperTheme {
        SplashScreen(onFinished = { /* no-op in preview */ })
    }
}

@Preview(name = "Login", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun LoginScreenPreview() {
    HomieeHelperTheme {
        LoginScreen(
            onLoginClick = {},
            onGoogleLoginClick = {},
            onForgotPasswordClick = {},
            onSignUpClick = {}
        )
    }
}

@Preview(name = "Sign Up", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun SignUpScreenPreview() {
    HomieeHelperTheme {
        SignUpScreen(
            onBackClick = {},
            onSignUpClick = {},
            onGoogleSignUpClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(name = "Verify OTP", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun OtpScreenPreview() {
    HomieeHelperTheme {
        OtpScreen(
            email = "john.doe@example.com",
            onBackClick = {},
            onVerifyClick = {}
        )
    }
}

@Preview(name = "Forgot Password", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun ForgotPasswordScreenPreview() {
    HomieeHelperTheme {
        ForgotPasswordScreen(
            onBackClick = {},
            onSendResetLinkClick = {}
        )
    }
}

@Preview(name = "Reset Password", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun ResetPasswordScreenPreview() {
    HomieeHelperTheme {
        ResetPasswordScreen(
            onBackClick = {},
            onUpdatePasswordClick = {}
        )
    }
}

