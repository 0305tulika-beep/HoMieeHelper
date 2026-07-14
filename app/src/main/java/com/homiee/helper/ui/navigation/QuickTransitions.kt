package com.homiee.helper.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

/**
 * Quick, snappy screen transitions for the whole app.
 *
 * Jetpack Navigation-Compose's default transition is a ~300ms slide that
 * feels sluggish for a simple auth flow. These fade transitions are short
 * (110ms) so navigating between screens feels instant rather than "smooth".
 * Applied once at the NavHost level in [HomieeNavHost] so every destination
 * in the graph automatically inherits the same quick behaviour.
 */
private const val QUICK_DURATION_MS = 110

val quickEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(animationSpec = tween(durationMillis = QUICK_DURATION_MS, easing = LinearEasing))
}

val quickExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(animationSpec = tween(durationMillis = QUICK_DURATION_MS, easing = LinearEasing))
}

val quickPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    quickEnterTransition

val quickPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    quickExitTransition
