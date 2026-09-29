package com.homiee.helper.ui.screens.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.homiee.helper.data.repository.UserDetailsRepository
import com.homiee.helper.viewmodel.ProfileViewModel

/**
 * Previews for the Home / Job Requests / My Jobs / Messages / Profile
 * dashboard flow, plus every full-screen detail page it opens into.
 *
 * All callbacks are no-ops - these exist purely to check layout, spacing,
 * and the transparent-status-bar / white-nav-bar look while iterating on UI.
 */

private const val PREVIEW_WIDTH_DP = 360
private const val PREVIEW_HEIGHT_DP = 800

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
    HomieeHelperTheme {
        JobRequestsScreen()
    }
}

@Preview(name = "Request Details", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun RequestDetailsScreenPreview() {
    HomieeHelperTheme {
        RequestDetailsScreen(
            requestId = HelperSampleData.newRequests.first().id,
            onBackClick = {},
            onAcceptRequest = {},
            onRejectRequest = {}
        )
    }
}

@Preview(name = "My Jobs - Upcoming", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun MyJobsScreenUpcomingPreview() {
    HomieeHelperTheme {
        MyJobsScreen(initialTabIndex = 0)
    }
}

@Preview(name = "My Jobs - Active", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun MyJobsScreenActivePreview() {
    HomieeHelperTheme {
        MyJobsScreen(initialTabIndex = 1)
    }
}

@Preview(name = "My Jobs - Completed", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun MyJobsScreenCompletedPreview() {
    HomieeHelperTheme {
        MyJobsScreen(initialTabIndex = 2)
    }
}

@Preview(name = "Job Details", showBackground = true, widthDp = PREVIEW_WIDTH_DP, heightDp = PREVIEW_HEIGHT_DP)
@Composable
fun JobDetailsScreenPreview() {
    HomieeHelperTheme {
        JobDetailsScreen(
            jobId = HelperSampleData.completedBookings.first().id,
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
    HomieeHelperTheme {
        VerifiedDocumentsBody(
            profile = HelperProfileResponse(
                govt_id_type = "aadhaar",
                govt_id_number = "1234 5678 9012",
                front_card = "/media/helper_docs/front/aadhaar.jpg",
                back_card = "/media/helper_docs/back/aadhaar.jpg",
                police_verification_cert = "/media/helper_docs/police/cert.pdf"
            ),
            isLoading = false,
            errorMessage = null,
            onRetry = {},
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