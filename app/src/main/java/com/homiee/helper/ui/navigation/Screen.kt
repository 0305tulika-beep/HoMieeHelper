package com.homiee.helper.ui.navigation

sealed class Screen(val route: String) {
    // ---- Auth flow ----
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")

    data object Otp : Screen("otp/{email}") {
        fun createRoute(email: String) = "otp/${email.ifBlank { "unknown" }}"
    }

    data object ForgotPassword : Screen("forgot_password")
    data object ResetPassword : Screen("reset_password")

    // ---- Onboarding / worker profile form flow ----
    data object PersonalInformation : Screen("personal_information")
    data object AddressInformation : Screen("address_information")
    data object Documents : Screen("documents")
    data object ServicesPricing : Screen("services_pricing")
    data object ExperienceAbout : Screen("experience_about")
    data object Availability : Screen("availability")

    // ---- Helper dashboard flow ----
    data object Home : Screen("home")

    data object JobRequests : Screen("job_requests")
    data object RequestDetails : Screen("request_details/{requestId}") {
        fun createRoute(requestId: String) = "request_details/$requestId"
    }

    data object MyJobs : Screen("my_jobs")
    data object JobDetails : Screen("job_details/{jobId}") {
        fun createRoute(jobId: String) = "job_details/$jobId"
    }

    data object Messages : Screen("messages")
    data object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }

    data object Profile : Screen("profile")
    data object VerifiedDocuments : Screen("verified_documents")
    data object TotalEarnings : Screen("total_earnings")
}