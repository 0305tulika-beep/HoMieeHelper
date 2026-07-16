package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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

@Composable
fun JobRequestsScreen(
    onBackToHome: () -> Unit = {},
    onOpenRequest: (String) -> Unit = {},
    onAcceptRequest: (String) -> Unit = {},
    onRejectRequest: (String) -> Unit = {},
    currentRoute: String? = null,
    onNavItemClick: (HelperNavItem) -> Unit = {}
) {
    DashboardSystemBars(darkStatusBarIcons = false)

    // TODO: This list is local UI state only. Accepting a request removes it from
    // this screen but does NOT yet persist anywhere shared. To make an accepted
    // request actually show up on the "Upcoming Jobs" screen, this needs to move
    // to a shared ViewModel/repository (e.g. a HelperViewModel exposing both
    // pendingRequests and upcomingBookings as StateFlow) so both screens read
    // from the same source of truth instead of each pulling their own static
    // list from HelperSampleData.
    val requests = remember { mutableStateListOf(*HelperSampleData.newRequests.toTypedArray()) }

    Scaffold(
        containerColor = BackgroundWhite,
        bottomBar = {
            HelperBottomNavBar(
                currentRoute = currentRoute,
                badgeCounts = mapOf(HelperNavItem.JobRequests to requests.size),
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Gradient header — background extends behind the status bar itself,
            // only the inner text content gets inset-padded, so there's no gap of
            // plain Scaffold background showing above it.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(TealPrimary, TealPrimaryDark)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Text("Job Requests", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Review and respond to new job requests from residents.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            if (requests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No new requests right now.", fontSize = 13.sp, color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests, key = { it.id }) { request ->
                        JobRequestCard(
                            request = request,
                            onClick = { onOpenRequest(request.id) },
                            onAccept = {
                                requests.remove(request)
                                onAcceptRequest(request.id)
                                // TODO: also add `request` (converted to a JobBooking) to the
                                // shared upcoming-bookings source once that's wired up.
                            },
                            onReject = {
                                requests.remove(request)
                                onRejectRequest(request.id)
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun JobRequestCard(
    request: JobRequest,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    SectionCard(
        modifier = Modifier
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            ServiceIconBadge(icon = request.service.icon)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                StatusChip(text = "New", background = InfoCardBg, textColor = TealPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(request.service.label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(request.location, fontSize = 12.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoChip(text = request.date)
            InfoChip(text = request.time)
            InfoChip(text = request.duration)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onReject,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SosRed)
            ) { Text("Reject", fontSize = 13.sp) }
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