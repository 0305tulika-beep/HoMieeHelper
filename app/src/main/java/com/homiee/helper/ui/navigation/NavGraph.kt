package com.homiee.helper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.homiee.helper.ui.components.HelperNavItem

// ---- Auth screens ----
import com.homiee.helper.ui.screens.auth.ForgotPasswordScreen
import com.homiee.helper.ui.screens.auth.LoginScreen
import com.homiee.helper.ui.screens.auth.OtpScreen
import com.homiee.helper.ui.screens.auth.ResetPasswordScreen
import com.homiee.helper.ui.screens.auth.SignUpScreen
import com.homiee.helper.ui.screens.auth.SplashScreen

// ---- Onboarding / form screens ----
import com.homiee.helper.ui.screens.forms.AddressInformationScreen
import com.homiee.helper.ui.screens.forms.AvailabilityScreen
import com.homiee.helper.ui.screens.forms.DocumentsScreen
import com.homiee.helper.ui.screens.forms.ExperienceAboutScreen
import com.homiee.helper.ui.screens.forms.PersonalInformationScreen
import com.homiee.helper.ui.screens.forms.ServicesPricingScreen

// ---- Helper dashboard screens ----
import com.homiee.helper.ui.screens.helper.ChatScreen
import com.homiee.helper.ui.screens.helper.HomeScreen
import com.homiee.helper.ui.screens.helper.JobDetailsScreen
import com.homiee.helper.ui.screens.helper.JobRequestsScreen
import com.homiee.helper.ui.screens.helper.MessagesScreen
import com.homiee.helper.ui.screens.helper.MyJobsScreen
import com.homiee.helper.ui.screens.helper.ProfileScreen
import com.homiee.helper.ui.screens.helper.RequestDetailsScreen
import com.homiee.helper.ui.screens.helper.TotalEarningsScreen
import com.homiee.helper.ui.screens.helper.VerifiedDocumentsScreen

@Composable
fun HomieeNavHost(navController: NavHostController = rememberNavController()) {
    // Shared bottom-nav click handler for Home / Job Requests / My Jobs / Messages / Profile.
    // "Single top + restore state" so switching tabs doesn't pile up the back stack and each
    // tab keeps its own scroll/tab-selection state.
    val onBottomNavClick: (HelperNavItem) -> Unit = { item ->
        navController.navigate(item.route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        // Quick fade transitions applied once here, inherited by every screen below.
        enterTransition = quickEnterTransition,
        exitTransition = quickExitTransition,
        popEnterTransition = quickPopEnterTransition,
        popExitTransition = quickPopExitTransition
    ) {

        // ================= AUTH FLOW =================

        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    // Flow starts at Sign Up, not Login. Splash is cleared from the
                    // stack so pressing back on SignUp closes the app instead of
                    // returning to Splash.
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    // Login button drops the user straight into the onboarding
                    // form flow (Form 1), clearing the entire auth stack behind them.
                    navController.navigate(Screen.PersonalInformation.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onGoogleLoginClick = {
                    navController.navigate(Screen.PersonalInformation.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                onSignUpClick = {
                    // Plain push - back from SignUp returns to Login.
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpClick = { email -> navController.navigate(Screen.Otp.createRoute(email)) },
                onGoogleSignUpClick = { /* TODO: wire up Google sign-up */ },
                onLoginClick = {
                    // Plain push - back from Login returns to SignUp.
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(
            route = Screen.Otp.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            OtpScreen(
                email = email,
                onBackClick = { navController.popBackStack() },
                onVerifyClick = {
                    // After verifying, send the new user into the profile-setup form flow,
                    // clearing the whole auth stack behind them.
                    navController.navigate(Screen.PersonalInformation.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendResetLinkClick = { navController.navigate(Screen.ResetPassword.route) }
            )
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onUpdatePasswordClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ================= ONBOARDING / FORM FLOW (Steps 1-6) =================

        composable(Screen.PersonalInformation.route) {
            PersonalInformationScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.AddressInformation.route) }
            )
        }

        composable(Screen.AddressInformation.route) {
            AddressInformationScreen(
                onBack = { navController.popBackStack() },
                onSkip = { navController.navigate(Screen.Documents.route) },
                onContinue = { navController.navigate(Screen.Documents.route) }
            )
        }

        composable(Screen.Documents.route) {
            DocumentsScreen(
                onBack = { navController.popBackStack() },
                onSkip = { navController.navigate(Screen.ServicesPricing.route) },
                onContinue = { navController.navigate(Screen.ServicesPricing.route) }
            )
        }

        composable(Screen.ServicesPricing.route) {
            ServicesPricingScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.ExperienceAbout.route) }
            )
        }

        composable(Screen.ExperienceAbout.route) {
            ExperienceAboutScreen(
                onBack = { navController.popBackStack() },
                onSkip = { navController.navigate(Screen.Availability.route) },
                onContinue = { navController.navigate(Screen.Availability.route) }
            )
        }

        composable(Screen.Availability.route) {
            AvailabilityScreen(
                onBack = { navController.popBackStack() },
                onSkip = {
                    // Onboarding finished (skipped last step) - drop into the dashboard.
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onContinue = {
                    // Onboarding finished - drop into the dashboard.
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ================= HELPER DASHBOARD FLOW (bottom-nav tabs) =================

        composable(Screen.Home.route) {
            val currentRoute by navController.currentBackStackEntryAsState()
            HomeScreen(
                onNotificationsClick = { /* TODO: notifications */ },
                onCompleteProfileClick = { navController.navigate(Screen.PersonalInformation.route) },
                onViewRequest = { id -> navController.navigate(Screen.RequestDetails.createRoute(id)) },
                onAcceptRequest = { /* TODO: accept request */ },
                onViewAllRequests = { onBottomNavClick(HelperNavItem.JobRequests) },
                onViewAllUpcoming = { onBottomNavClick(HelperNavItem.MyJobs) },
                onUpcomingJobClick = { id -> navController.navigate(Screen.JobDetails.createRoute(id)) },
                onSosClick = { /* TODO: SOS flow */ },
                currentRoute = currentRoute?.destination?.route,
                onNavItemClick = onBottomNavClick
            )
        }

        composable(Screen.JobRequests.route) {
            val currentRoute by navController.currentBackStackEntryAsState()
            JobRequestsScreen(
                onOpenRequest = { id -> navController.navigate(Screen.RequestDetails.createRoute(id)) },
                onAcceptRequest = { /* TODO: accept request */ },
                onRejectRequest = { /* TODO: reject request */ },
                currentRoute = currentRoute?.destination?.route,
                onNavItemClick = onBottomNavClick
            )
        }

        composable(
            route = Screen.RequestDetails.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId").orEmpty()
            RequestDetailsScreen(
                requestId = requestId,
                onBackClick = { navController.popBackStack() },
                onAcceptRequest = { navController.popBackStack() },
                onRejectRequest = { navController.popBackStack() }
            )
        }

        composable(Screen.MyJobs.route) {
            val currentRoute by navController.currentBackStackEntryAsState()
            MyJobsScreen(
                onOpenJob = { id -> navController.navigate(Screen.JobDetails.createRoute(id)) },
                onChatWithResident = { id -> navController.navigate(Screen.Chat.createRoute(id)) },
                onCancelJob = { /* TODO: cancel job */ },
                onContactResident = { /* TODO: contact resident */ },
                currentRoute = currentRoute?.destination?.route,
                onNavItemClick = onBottomNavClick
            )
        }

        composable(
            route = Screen.JobDetails.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId").orEmpty()
            JobDetailsScreen(
                jobId = jobId,
                onBackClick = { navController.popBackStack() },
                onChatWithResident = { id -> navController.navigate(Screen.Chat.createRoute(id)) },
                onCallResident = { /* TODO: place call */ }
            )
        }

        composable(Screen.Messages.route) {
            val currentRoute by navController.currentBackStackEntryAsState()
            MessagesScreen(
                onOpenChat = { id -> navController.navigate(Screen.Chat.createRoute(id)) },
                onNewMessageClick = { /* TODO: start new conversation */ },
                currentRoute = currentRoute?.destination?.route,
                onNavItemClick = onBottomNavClick
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId").orEmpty()
            ChatScreen(
                conversationId = conversationId,
                onBackClick = { navController.popBackStack() },
                onCallClick = { /* TODO: place call */ }
            )
        }

        composable(Screen.Profile.route) {
            val currentRoute by navController.currentBackStackEntryAsState()
            ProfileScreen(
                onViewVerifiedDocuments = { navController.navigate(Screen.VerifiedDocuments.route) },
                onViewTotalEarnings = { navController.navigate(Screen.TotalEarnings.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDeactivateAccount = { /* TODO: deactivate account */ },
                onDeleteAccount = { /* TODO: delete account */ },
                onContactSupport = { /* TODO: contact support */ },
                currentRoute = currentRoute?.destination?.route,
                onNavItemClick = onBottomNavClick
            )
        }

        composable(Screen.VerifiedDocuments.route) {
            VerifiedDocumentsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.TotalEarnings.route) {
            TotalEarningsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}