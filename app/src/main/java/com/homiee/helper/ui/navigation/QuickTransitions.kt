package com.homiee.helper.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry

/**
 * No transition at all — one screen is simply replaced by the next with no
 * fade, slide, or any other animation. Applied once at the NavHost level in
 * [HomieeNavHost] so every destination in the graph automatically inherits
 * this instant-switch behaviour.
 */
val quickEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    EnterTransition.None
}

val quickExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    ExitTransition.None
}

val quickPopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    EnterTransition.None
}

val quickPopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    ExitTransition.None
}