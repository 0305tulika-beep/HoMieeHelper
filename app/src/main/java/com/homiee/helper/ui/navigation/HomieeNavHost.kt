package com.homiee.helper.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.homiee.helper.data.local.TokenManager
import com.homiee.helper.ui.components.HelperNavItem
import com.homiee.helper.viewmodel.ProfileViewModel

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
import com.homiee.helper.viewmodel.AccountActionViewModel
import com.homiee.helper.viewmodel.AddressInformationViewModel
import com.homiee.helper.viewmodel.AvailabilityViewModel
import com.homiee.helper.viewmodel.DocumentsViewModel
import com.homiee.helper.viewmodel.ExperienceAboutViewModel
import com.homiee.helper.viewmodel.PersonalInformationViewModel
import com.homiee.helper.viewmodel.ServicesPricingViewModel

@Composable
fun HomieeNavHost(navController: NavHostController = rememberNavController()) {
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
        enterTransition = quickEnterTransition,
        exitTransition = quickExitTransition,
        popEnterTransition = quickPopEnterTransition,
        popExitTransition = quickPopExitTransition
    ) {

        // ================= AUTH FLOW =================

        composable(Screen.Splash.route) {
            val context = LocalContext.current
            SplashScreen(
                onFinished = {
                    val tokenManager = TokenManager.getInstance(context)
                    val destination = when {
                        !tokenManager.isLoggedIn() -> Screen.SignUp.route
                        !tokenManager.isOnboardingComplete() -> Screen.PersonalInformation.route
                        else -> Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    // A successful login is an existing, already-onboarded helper —
                    // send them straight to the dashboard, clearing the whole auth stack.
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onGoogleLoginClick = {
                    // TODO: Google sign-in isn't wired to a real account yet.
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                onSignUpClick = {
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
                    // Clears the ENTIRE stack (Splash, SignUp, Otp) — PersonalInformation
                    // becomes the sole entry, so there's nothing left to back into.
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
            val activity = LocalContext.current as? Activity
            val context = LocalContext.current
            val personalInfoViewModel: PersonalInformationViewModel =
                viewModel(factory = PersonalInformationViewModel.Factory(context))
            PersonalInformationScreen(
                viewModel = personalInfoViewModel,
                // First form screen: stack was cleared via popUpTo(0) on the way here,
                // so there's nothing to pop back into — close the app instead.
                onBack = { activity?.finish() },
                onContinue = { navController.navigate(Screen.AddressInformation.route) }
            )
        }

        composable(Screen.AddressInformation.route) {
            val context = LocalContext.current
            val addressViewModel: AddressInformationViewModel =
                viewModel(factory = AddressInformationViewModel.Factory(context))
            AddressInformationScreen(
                viewModel = addressViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.Documents.route) }
            )
        }

        composable(Screen.Documents.route) {
            val context = LocalContext.current
            val documentsViewModel: DocumentsViewModel =
                viewModel(factory = DocumentsViewModel.Factory(context))
            DocumentsScreen(
                viewModel = documentsViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.ServicesPricing.route) }
            )
        }

        composable(Screen.ServicesPricing.route) {
            val context = LocalContext.current
            val servicesPricingViewModel: ServicesPricingViewModel =
                viewModel(factory = ServicesPricingViewModel.Factory(context))
            ServicesPricingScreen(
                viewModel = servicesPricingViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.ExperienceAbout.route) }
            )
        }

        composable(Screen.ExperienceAbout.route) {
            val context = LocalContext.current
            val experienceAboutViewModel: ExperienceAboutViewModel =
                viewModel(factory = ExperienceAboutViewModel.Factory(context))
            ExperienceAboutScreen(
                viewModel = experienceAboutViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.Availability.route) }
            )
        }

        composable(Screen.Availability.route) {
            val context = LocalContext.current
            val availabilityViewModel: AvailabilityViewModel =
                viewModel(factory = AvailabilityViewModel.Factory(context))
            AvailabilityScreen(
                viewModel = availabilityViewModel,
                onBack = { navController.popBackStack() },
                onContinue = {
                    TokenManager.getInstance(context).setOnboardingComplete(true)
                    // Onboarding fully done — clears the whole form stack behind the user.
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
            val context = LocalContext.current
            val currentRoute by navController.currentBackStackEntryAsState()
            val accountViewModel: AccountActionViewModel =
                viewModel(factory = AccountActionViewModel.Factory(context))
            val profileViewModel: ProfileViewModel =
                viewModel(factory = ProfileViewModel.Factory(context))
            // Email saved at sign-up (OTP verify) / login
            val email = remember { TokenManager.getInstance(context).getUserEmail() }

            val goToLoginCleared: () -> Unit = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            ProfileScreen(
                profileViewModel = profileViewModel,
                email = email,
                onViewVerifiedDocuments = { navController.navigate(Screen.VerifiedDocuments.route) },
                onViewTotalEarnings = { navController.navigate(Screen.TotalEarnings.route) },
                accountViewModel = accountViewModel,
                onAccountCleared = goToLoginCleared,
                onContactSupport = { /* TODO: contact support */ },
                currentRoute = currentRoute?.destination?.route,
                onNavItemClick = onBottomNavClick
            )
        }

        composable(Screen.VerifiedDocuments.route) { backStackEntry ->
            val context = LocalContext.current
            val profileEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Profile.route)
            }
            val profileViewModel: ProfileViewModel =
                viewModel(
                    viewModelStoreOwner = profileEntry,
                    factory = ProfileViewModel.Factory(context)
                )
            VerifiedDocumentsScreen(
                profileViewModel = profileViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.TotalEarnings.route) {
            TotalEarningsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}