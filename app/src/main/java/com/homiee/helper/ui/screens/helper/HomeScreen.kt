package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

@Composable
fun HomeScreen(
    userName: String = "Priya",
    profileCompletionPercent: Int = 60,
    onNotificationsClick: () -> Unit = {},
    onCompleteProfileClick: () -> Unit = {},
    onViewRequest: (String) -> Unit = {},
    onAcceptRequest: (String) -> Unit = {},
    onViewAllRequests: () -> Unit = {},
    onViewAllUpcoming: () -> Unit = {},
    onUpcomingJobClick: (String) -> Unit = {},
    onSosClick: () -> Unit = {},
    currentRoute: String? = null,
    onNavItemClick: (HelperNavItem) -> Unit = {}
) {
    DashboardSystemBars(darkStatusBarIcons = false)
    var isOnline by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = BackgroundWhite,
        bottomBar = {
            HelperBottomNavBar(
                currentRoute = currentRoute,
                badgeCounts = mapOf(HelperNavItem.JobRequests to HelperSampleData.newRequests.size),
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Teal gradient header - extends behind the transparent status bar.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(TealPrimary, TealPrimaryDark)))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Good morning, $userName \uD83D\uDC4B",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Great helpers make happy homes.\nLet's get you more opportunities!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { onNotificationsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.NotificationsNone, contentDescription = "Notifications", tint = Color.White)
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                // Complete Your Profile
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Complete Your Profile", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Add remaining details to unlock more job opportunities.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        ProgressRing(percent = profileCompletionPercent)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    PrimaryButton(text = "Complete Now", onClick = onCompleteProfileClick)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Go Online toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(TealPale)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Go Online", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Residents can see you and send job requests",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = isOnline,
                        onCheckedChange = { isOnline = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TealPrimary,
                            uncheckedTrackColor = BorderGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // New Requests
                SectionHeader(
                    title = "New Requests",
                    actionText = "${HelperSampleData.newRequests.size} New",
                    onActionClick = onViewAllRequests
                )
                Spacer(modifier = Modifier.height(12.dp))
                HelperSampleData.newRequests.take(3).forEach { request ->
                    HomeRequestRow(
                        request = request,
                        onView = { onViewRequest(request.id) },
                        onAccept = { onAcceptRequest(request.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Text(
                    text = "View All Requests",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealPrimary,
                    modifier = Modifier.clickable { onViewAllRequests() }
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Upcoming Jobs
                SectionHeader(title = "Upcoming Jobs", actionText = "View All", onActionClick = onViewAllUpcoming)
                Spacer(modifier = Modifier.height(12.dp))
                HelperSampleData.upcomingBookings.take(2).forEach { booking ->
                    HomeUpcomingJobRow(
                        booking = booking,
                        onClick = { onUpcomingJobClick(booking.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Emergency SOS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SosRedBg)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SosRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SOS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Emergency SOS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SosRed)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Tap to alert your emergency contacts in case of any urgent situation.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                    OutlinedButton(
                        onClick = onSosClick,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SosRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SosRed)
                    ) {
                        Icon(Icons.Filled.Sos, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tap to SOS", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HomeRequestRow(request: JobRequest, onView: () -> Unit, onAccept: () -> Unit) {
    SectionCard {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(request.residentName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(request.location, fontSize = 12.sp, color = TextSecondary)
            }
            Text(request.timeAgo, fontSize = 11.sp, color = HintGray)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onView,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
            ) { Text("View", fontSize = 13.sp) }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = onAccept,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) { Text("Accept", fontSize = 13.sp, color = Color.White) }
        }
    }
}

@Composable
private fun HomeUpcomingJobRow(booking: JobBooking, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TealPale)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(46.dp)) {
            Text(booking.date.substringBefore(" "), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TealPrimaryDark)
            Text(booking.date.substringAfter(" ").substringBefore(" "), fontSize = 11.sp, color = TextSecondary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(booking.service.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(booking.residentName, fontSize = 12.sp, color = TextSecondary)
            Text(booking.location, fontSize = 11.sp, color = HintGray)
        }
        StatusChip(text = "Confirmed", background = SuccessGreenBg, textColor = SuccessGreen)
    }
}