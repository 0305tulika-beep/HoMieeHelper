package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import com.homiee.helper.viewmodel.HelperBookingViewModel

private enum class JobsTab(val label: String) { UPCOMING("Upcoming"), ACTIVE("Active"), COMPLETED("Completed") }

@Composable
fun MyJobsScreen(
    viewModel: HelperBookingViewModel,
    onOpenJob: (String) -> Unit = {},
    onChatWithResident: (String) -> Unit = {},
    currentRoute: String? = null,
    onNavItemClick: (HelperNavItem) -> Unit = {},
    /** 0 = Upcoming, 1 = Active, 2 = Completed. Mainly useful for previews/deep-links. */
    initialTabIndex: Int = 0
) {
    DashboardSystemBars(darkStatusBarIcons = false)
    var selectedTab by remember { mutableStateOf(JobsTab.entries[initialTabIndex.coerceIn(0, JobsTab.entries.lastIndex)]) }

    val pending by viewModel.pendingRequests.collectAsState()
    val upcomingDtos by viewModel.upcoming.collectAsState()
    val completedDtos by viewModel.completed.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    // Fetch fresh jobs every time this screen opens
    LaunchedEffect(Unit) { viewModel.load() }

    val upcomingJobs = remember(upcomingDtos) { upcomingDtos.map { it.toJobBooking() } }
    val completedJobs = remember(completedDtos) { completedDtos.map { it.toJobBooking() } }

    Scaffold(
        containerColor = BackgroundWhite,
        bottomBar = {
            HelperBottomNavBar(
                currentRoute = currentRoute,
                badgeCounts = mapOf(HelperNavItem.JobRequests to pending.size),
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(TealPrimary, TealPrimaryDark)))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Text("My Jobs", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Manage your bookings and keep your schedule on track.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Segmented tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TealPale)
                    .padding(4.dp)
            ) {
                JobsTab.entries.forEach { tab ->
                    val selected = tab == selectedTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) TealPrimary else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tab.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else TealPrimaryDark
                        )
                    }
                }
            }

            when (selectedTab) {
                JobsTab.UPCOMING -> JobsListSection(
                    title = "Upcoming Bookings",
                    jobs = upcomingJobs,
                    isLoading = isLoading,
                    loadError = loadError,
                    emptyText = "No upcoming jobs",
                    statusText = "Confirmed",
                    statusBg = SuccessGreenBg,
                    statusColor = SuccessGreen,
                    onRetry = { viewModel.load() },
                    onOpenJob = onOpenJob,
                    onChat = onChatWithResident
                )

                // The bookings API has no "in progress" status, so there is nothing to show yet.
                JobsTab.ACTIVE -> CenteredBox {
                    Text("No active job right now", fontSize = 14.sp, color = TextSecondary)
                }

                JobsTab.COMPLETED -> JobsListSection(
                    title = "Completed Bookings",
                    jobs = completedJobs,
                    isLoading = isLoading,
                    loadError = loadError,
                    emptyText = "No completed jobs yet",
                    statusText = "Completed",
                    statusBg = InfoCardBg,
                    statusColor = TealPrimary,
                    onRetry = { viewModel.load() },
                    onOpenJob = onOpenJob,
                    onChat = onChatWithResident
                )
            }
        }
    }
}

@Composable
private fun CenteredBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
private fun JobsListSection(
    title: String,
    jobs: List<JobBooking>,
    isLoading: Boolean,
    loadError: String?,
    emptyText: String,
    statusText: String,
    statusBg: Color,
    statusColor: Color,
    onRetry: () -> Unit,
    onOpenJob: (String) -> Unit,
    onChat: (String) -> Unit
) {
    when {
        // First load
        isLoading && jobs.isEmpty() -> CenteredBox {
            CircularProgressIndicator(color = TealPrimary)
        }

        // Failed with nothing to show
        loadError != null && jobs.isEmpty() -> Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = loadError, fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(onClick = onRetry) {
                Text("Retry", color = TealPrimary, fontWeight = FontWeight.Bold)
            }
        }

        jobs.isEmpty() -> CenteredBox {
            Text(emptyText, fontSize = 14.sp, color = TextSecondary)
        }

        else -> LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SectionLabel(title = title) }
            items(jobs, key = { it.id }) { booking ->
                BookingCard(
                    booking = booking,
                    statusText = statusText,
                    statusBg = statusBg,
                    statusColor = statusColor,
                    primaryActionLabel = "Chat",
                    onPrimaryAction = { onChat(booking.id) },
                    secondaryActionLabel = "View Details",
                    onSecondaryAction = { onOpenJob(booking.id) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun BookingCard(
    booking: JobBooking,
    statusText: String,
    statusBg: Color,
    statusColor: Color,
    primaryActionLabel: String,
    onPrimaryAction: () -> Unit,
    secondaryActionLabel: String,
    onSecondaryAction: () -> Unit,
    tertiaryActionLabel: String? = null,
    onTertiaryAction: () -> Unit = {}
) {
    // Same subtle 1dp shadow used for cards on the Home and Job Requests screens.
    SectionCard(
        modifier = Modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = false)
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            ServiceIconBadge(icon = booking.service.icon)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(booking.service.label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(booking.residentName, fontSize = 12.sp, color = TextSecondary)
                if (booking.location.isNotBlank()) {
                    Text(booking.location, fontSize = 11.sp, color = HintGray)
                }
            }
            StatusChip(text = statusText, background = statusBg, textColor = statusColor)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoChip(text = booking.date)
            InfoChip(text = booking.time)
            if (booking.duration.isNotBlank()) InfoChip(text = booking.duration)
        }
        Spacer(modifier = Modifier.height(12.dp))
        // All actions share a single row.
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onPrimaryAction,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
            ) { Text(primaryActionLabel, fontSize = 12.sp, maxLines = 1) }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onSecondaryAction,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
            ) { Text(secondaryActionLabel, fontSize = 12.sp, maxLines = 1) }
            if (tertiaryActionLabel != null) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onTertiaryAction,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SosRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SosRed)
                ) { Text(tertiaryActionLabel, fontSize = 12.sp, maxLines = 1) }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ChipBlueBg)
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Text(text = text, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}