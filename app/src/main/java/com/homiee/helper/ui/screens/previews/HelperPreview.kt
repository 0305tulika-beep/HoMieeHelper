package com.homiee.helper.ui.screens.previews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.homiee.helper.data.model.BookingDto
import com.homiee.helper.data.repository.UserDetailsRepository
import com.homiee.helper.ui.screens.helper.ChatScreen
import com.homiee.helper.ui.screens.helper.HelperSampleData
import com.homiee.helper.ui.screens.helper.HomeScreen
import com.homiee.helper.ui.screens.helper.JobDetailsScreen
import com.homiee.helper.ui.screens.helper.JobRequestsScreen
import com.homiee.helper.ui.screens.helper.MessagesScreen
import com.homiee.helper.ui.screens.helper.MyJobsScreen
import com.homiee.helper.ui.screens.helper.ProfileScreen
import com.homiee.helper.ui.screens.helper.RequestDetailsScreen
import com.homiee.helper.ui.screens.helper.TotalEarningsScreen
import com.homiee.helper.ui.screens.helper.VerifiedDocumentsScreen
import com.homiee.helper.ui.theme.HomieeHelperTheme
import com.homiee.helper.viewmodel.HelperBookingViewModel
import com.homiee.helper.viewmodel.ProfileViewModel

/**
 * Previews for the Home / Job Requests / My Jobs / Messages / Profile
 * dashboard flow, plus every full-screen detail page it opens into.
 *
 * All callbacks are no-ops - these exist purely to check layout, spacing,
 * and the transparent-status-bar / white-nav-bar look while iterating on UI.
 *
 * Screens that read bookings get a HelperBookingViewModel seeded with the
 * sample bookings below (see PREVIEW_BOOKINGS). It never touches the network.
 */

private const val PREVIEW_WIDTH_DP = 360
private const val PREVIEW_HEIGHT_DP = 800

// Ids used by the detail previews below
private const val PREVIEW_PENDING_ID = "1"
private const val PREVIEW_UPCOMING_ID = "3"
private const val PREVIEW_COMPLETED_ID = "5"

private val PREVIEW_BOOKINGS = listOf(
    // Pending requests
    BookingDto(
        id = 1, residentId = 3, residentName = "Neha Sharma", helperId = 12, helperName = "Priya Verma",
        serviceId = 1, service = "Cleaning", bookingDate = "2026-07-12",
        startTime = "10:00:00", endTime = "12:00:00",
        specialInstructions = "Please focus on kitchen cleaning and bathrooms. Bring your own cleaning materials.",
        totalAmount = "500.00", status = "pending", createdAt = "2026-07-09T14:00:00Z"
    ),
    BookingDto(
        id = 2, residentId = 4, residentName = "Ritu Verma", helperId = 12, helperName = "Priya Verma",
        serviceId = 2, service = "Cooking", bookingDate = "2026-07-13",
        startTime = "14:00:00", endTime = "17:00:00",
        totalAmount = "600.00", status = "pending", createdAt = "2026-07-09T13:00:00Z"
    ),
    // Confirmed (upcoming)
    BookingDto(
        id = 3, residentId = 5, residentName = "Anjali Singh", helperId = 12, helperName = "Priya Verma",
        serviceId = 3, service = "Babysitting", bookingDate = "2026-07-14",
        startTime = "17:00:00", endTime = "21:00:00",
        specialInstructions = "Kids sleep at 9 PM.",
        totalAmount = "550.00", status = "confirmed", createdAt = "2026-07-08T10:00:00Z"
    ),
    BookingDto(
        id = 4, residentId = 6, residentName = "Sunita Agarwal", helperId = 12, helperName = "Priya Verma",
        serviceId = 4, service = "Eldercare", bookingDate = "2026-07-15",
        startTime = "09:00:00", endTime = "11:00:00",
        totalAmount = "500.00", status = "confirmed", createdAt = "2026-07-08T09:00:00Z"
    ),
    // Completed
    BookingDto(
        id = 5, residentId = 3, residentName = "Neha Sharma", helperId = 12, helperName = "Priya Verma",
        serviceId = 1, service = "Cleaning", bookingDate = "2026-07-10",
        startTime = "10:00:00", endTime = "12:00:00",
        specialInstructions = "Please focus on kitchen cleaning and bathrooms.",
        totalAmount = "500.00", status = "completed", createdAt = "2026-07-05T10:00:00Z"
    )
)

/** A ViewModel pre-filled with sample bookings; safe to build inside a @Preview. */
@Composable
private fun rememberPreviewBookingViewModel(): HelperBookingViewModel =
    remember { HelperBookingViewModel(initialBookings = PREVIEW_BOOKINGS) }

@Preview(name = "Home", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun HomeScreenPreview() {
    HomieeHelperTheme {
        HomeScreen()
    }
}

@Preview(name = "Job Requests", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun JobRequestsScreenPreview() {
    val viewModel = rememberPreviewBookingViewModel()
    HomieeHelperTheme {
        JobRequestsScreen(viewModel = viewModel)
    }
}

@Preview(name = "Request Details", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun RequestDetailsScreenPreview() {
    val viewModel = rememberPreviewBookingViewModel()
    HomieeHelperTheme {
        RequestDetailsScreen(
            requestId = PREVIEW_PENDING_ID,
            viewModel = viewModel,
            onBackClick = {}
        )
    }
}

@Preview(name = "My Jobs - Upcoming", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun MyJobsScreenUpcomingPreview() {
    val viewModel = rememberPreviewBookingViewModel()
    HomieeHelperTheme {
        MyJobsScreen(viewModel = viewModel, initialTabIndex = 0)
    }
}

@Preview(name = "My Jobs - Active", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun MyJobsScreenActivePreview() {
    val viewModel = rememberPreviewBookingViewModel()
    HomieeHelperTheme {
        MyJobsScreen(viewModel = viewModel, initialTabIndex = 1)
    }
}

@Preview(name = "My Jobs - Completed", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun MyJobsScreenCompletedPreview() {
    val viewModel = rememberPreviewBookingViewModel()
    HomieeHelperTheme {
        MyJobsScreen(viewModel = viewModel, initialTabIndex = 2)
    }
}

@Preview(name = "Job Details - Upcoming", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun JobDetailsUpcomingScreenPreview() {
    val viewModel = rememberPreviewBookingViewModel()
    HomieeHelperTheme {
        JobDetailsScreen(
            jobId = PREVIEW_UPCOMING_ID,
            viewModel = viewModel,
            onBackClick = {},
            onChatWithResident = {},
            onCallResident = {}
        )
    }
}

@Preview(name = "Job Details - Completed", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun JobDetailsCompletedScreenPreview() {
    val viewModel = rememberPreviewBookingViewModel()
    HomieeHelperTheme {
        JobDetailsScreen(
            jobId = PREVIEW_COMPLETED_ID,
            viewModel = viewModel,
            onBackClick = {},
            onChatWithResident = {},
            onCallResident = {}
        )
    }
}

@Preview(name = "Messages", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun MessagesScreenPreview() {
    HomieeHelperTheme {
        MessagesScreen()
    }
}

@Preview(name = "Chat", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun ChatScreenPreview() {
    HomieeHelperTheme {
        ChatScreen(
            conversationId = HelperSampleData.chatPreviews.first().id,
            onBackClick = {}
        )
    }
}

@Preview(name = "Profile", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun ProfileScreenPreview() {
    val context = LocalContext.current
    val profileViewModel = remember {
        ProfileViewModel(UserDetailsRepository(context.applicationContext))
    }
    HomieeHelperTheme {
        ProfileScreen(
            profileViewModel = profileViewModel,
            onViewVerifiedDocuments = {},
            onViewTotalEarnings = {},
            accountViewModel = null,
            onAccountCleared = {},
            onContactSupport = {}
        )
    }
}

@Preview(name = "Verified Documents", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun VerifiedDocumentsScreenPreview() {
    val context = LocalContext.current
    val profileViewModel = remember {
        ProfileViewModel(UserDetailsRepository(context.applicationContext))
    }
    HomieeHelperTheme {
        VerifiedDocumentsScreen(
            profileViewModel = profileViewModel,
            onBackClick = {}
        )
    }
}

@Preview(name = "Total Earnings", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun TotalEarningsScreenPreview() {
    HomieeHelperTheme {
        TotalEarningsScreen(onBackClick = {})
    }
}