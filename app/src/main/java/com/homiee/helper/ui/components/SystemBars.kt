package com.homiee.helper.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Shared system-bar controller used by the Sign Up, Login, OTP,
 * Forgot Password and Reset Password screens (via [AuthScaffold]).
 *
 * Makes both the status bar AND the navigation bar fully transparent so the
 * screen's own background shows through them edge-to-edge. Actual UI content
 * is kept clear of the bars separately, by padding with
 * `Modifier.windowInsetsPadding(WindowInsets.systemBars)` inside [AuthScaffold] -
 * so the bars are transparent, but content never sits underneath them.
 */
@Composable
fun TransparentSystemBars(useDarkIcons: Boolean = true) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as Activity).window

    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = useDarkIcons
        controller.isAppearanceLightNavigationBars = useDarkIcons
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
}

/**
 * Fully hides both the status bar and the navigation bar (immersive mode).
 * Used only on the Splash screen. Bars are restored automatically once the
 * user lands on any screen using [TransparentSystemBars].
 */
@Composable
fun HiddenSystemBars() {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as Activity).window

    DisposableEffect(Unit) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, view)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            // Next screen (AuthScaffold) re-shows + re-styles the bars itself.
        }
    }
}

/** Fully opaque white status + navigation bars (edge-to-edge disabled). */
@Composable
fun WhiteSystemBars() {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as Activity).window

    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.White.toArgb()
        window.navigationBarColor = Color.White.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }
}

/**
 * System bars for the Home / Job Requests / My Jobs / Messages / Profile
 * dashboard flow (everything behind [HelperBottomNavBar]).
 *
 * - Status bar: fully transparent, so a screen's own header (e.g. Home's
 *   teal gradient) shows straight through it for an edge-to-edge look.
 * - Navigation bar: solid WHITE, matching the white background
 *   [HelperBottomNavBar] sits on, so our bar visually "sits above" the
 *   system nav bar as one continuous white strip, elevated by its own
 *   shadow rather than blending transparently into it.
 *
 * Content is still edge-to-edge (`decorFitsSystemWindows = false`); each
 * screen is responsible for padding its own top content away from the
 * status bar via `Modifier.windowInsetsPadding(WindowInsets.statusBars)`,
 * and [HelperBottomNavBar] pads itself away from the nav bar via
 * `Modifier.windowInsetsPadding(WindowInsets.navigationBars)`.
 */
@Composable
fun DashboardSystemBars(darkStatusBarIcons: Boolean = false) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as Activity).window

    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.White.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        // Screens with a dark/teal header should keep light (white) status
        // bar icons (darkStatusBarIcons = false, the default). Screens with
        // a white top background should pass darkStatusBarIcons = true.
        controller.isAppearanceLightStatusBars = darkStatusBarIcons
        controller.isAppearanceLightNavigationBars = true
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
}