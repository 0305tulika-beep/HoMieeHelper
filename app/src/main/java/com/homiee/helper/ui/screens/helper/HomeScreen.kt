package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.R
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

@Composable
fun HomeScreen(
    userName: String = "Priya",
    profileCompletionPercent: Int = 60,
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
        // Transparent so the full-screen background image (below) shows through
        // everywhere the scrolling content doesn't cover it.
        containerColor = Color.Transparent,
        bottomBar = {
            HelperBottomNavBar(
                currentRoute = currentRoute,
                badgeCounts = mapOf(HelperNavItem.JobRequests to HelperSampleData.newRequests.size),
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-screen background image - fixed, sits behind everything,
            // does NOT scroll with the content.
            Image(
                painter = painterResource(id = R.drawable.homebg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                // Logo + tagline, sitting directly on the background image.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logotext1),
                        contentDescription = "Good morning, $userName",
                        modifier = Modifier.height(50.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Great helpers make happy homes.\nLet's get you more opportunities!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Complete Your Profile - white card, compacted into a single row: title, ring, button.
                    ElevatedHomeCard {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Complete Your Profile", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Unlock more job opportunities.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            ProgressRing(percent = profileCompletionPercent, size = 38.dp, strokeWidth = 4.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = onCompleteProfileClick,
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("Complete", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Go Online toggle - the only tinted top-level card, smaller text, smaller switch.
                    ElevatedHomeCard(background = TealPale) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Go Online", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Residents can see you and send job requests",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            Switch(
                                checked = isOnline,
                                onCheckedChange = { isOnline = it },
                                modifier = Modifier.scale(0.75f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = TealPrimary,
                                    uncheckedTrackColor = BorderGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // New Requests - white outer section card containing individually bordered request cards.
                    ElevatedHomeCard {
                        SectionHeader(
                            title = "New Requests",
                            onActionClick = onViewAllRequests
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        HelperSampleData.newRequests.take(3).forEachIndexed { index, request ->
                            HomeRequestCard(
                                request = request,
                                onView = { onViewRequest(request.id) },
                                onAccept = { onAcceptRequest(request.id) }
                            )
                            if (index != HelperSampleData.newRequests.take(3).lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "View All Requests",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealPrimary,
                            modifier = Modifier.clickable { onViewAllRequests() }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Upcoming Jobs - white outer section card; each job row inside is its own tinted card.
                    ElevatedHomeCard {
                        SectionHeader(title = "Upcoming Jobs", actionText = "View All", onActionClick = onViewAllUpcoming)
                        Spacer(modifier = Modifier.height(12.dp))
                        HelperSampleData.upcomingBookings.take(2).forEachIndexed { index, booking ->
                            HomeUpcomingJobRow(
                                booking = booking,
                                onClick = { onUpcomingJobClick(booking.id) }
                            )
                            if (index != HelperSampleData.upcomingBookings.take(2).lastIndex) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Emergency SOS - red tinted card.
                    ElevatedHomeCard(background = SosRedBg) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
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
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Card with a very subtle elevation (1.5dp) used to wrap every section on
 * the Home screen - Complete Your Profile, Go Online, New Requests,
 * Upcoming Jobs, and Emergency SOS.
 */
@Composable
private fun ElevatedHomeCard(
    background: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = background,
        tonalElevation = 0.dp,   // <-- kills the unwanted primary-tint overlay
        shadowElevation = 1.dp,  // <-- keeps the subtle shadow, no color shift
        content = { Column(modifier = Modifier.padding(16.dp), content = content) }
    )
}

/**
 * Individual new-request card: white background, subtle border, teal left
 * accent strip. Time-ago sits at the top; name/location and the View +
 * Accept buttons sit together in a single row below.
 */
@Composable
private fun HomeRequestCard(request: JobRequest, onView: () -> Unit, onAccept: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(TealPrimary)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.residentName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(request.location, fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedButton(
                    onClick = onView,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) { Text("View", fontSize = 12.sp) }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAccept,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) { Text("Accept", fontSize = 12.sp, color = Color.White) }
            }
        }
    }
}

/** Individual tinted job-row card inside the white "Upcoming Jobs" section. */
@Composable
private fun HomeUpcomingJobRow(booking: JobBooking, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TealPale)
            .clickable { onClick() }
            .padding(12.dp),
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