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
import com.homiee.helper.viewmodel.HelperBookingViewModel

@Composable
fun JobRequestsScreen(
    viewModel: HelperBookingViewModel,
    onBackToHome: () -> Unit = {},
    onOpenRequest: (String) -> Unit = {},
    currentRoute: String? = null,
    onNavItemClick: (HelperNavItem) -> Unit = {}
) {
    DashboardSystemBars(darkStatusBarIcons = false)

    val pending by viewModel.pendingRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val respondingIds by viewModel.respondingIds.collectAsState()

    // Fetch fresh requests every time this screen opens
    LaunchedEffect(Unit) { viewModel.load() }

    val requests = remember(pending) { pending.map { it.toJobRequest() } }

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

            when {
                // First load
                isLoading && requests.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimary)
                    }
                }

                // Failed with nothing to show
                loadError != null && requests.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = loadError ?: "", fontSize = 13.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = { viewModel.load() }) {
                            Text("Retry", color = TealPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                requests.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No new requests right now.", fontSize = 13.sp, color = TextSecondary)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(requests, key = { it.id }) { request ->
                            val bookingId = request.id.toIntOrNull()
                            JobRequestCard(
                                request = request,
                                isResponding = bookingId != null && bookingId in respondingIds,
                                onClick = { onOpenRequest(request.id) },
                                onAccept = { bookingId?.let { viewModel.accept(it) } },
                                onReject = { bookingId?.let { viewModel.reject(it) } }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun JobRequestCard(
    request: JobRequest,
    isResponding: Boolean,
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
                Text(request.residentName, fontSize = 12.sp, color = TextSecondary)
                if (request.location.isNotBlank()) {
                    Text(request.location, fontSize = 11.sp, color = HintGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoChip(text = request.date)
            InfoChip(text = request.time)
            if (request.duration.isNotBlank()) InfoChip(text = request.duration)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onReject,
                enabled = !isResponding,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SosRed)
            ) { Text("Reject", fontSize = 13.sp) }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = onAccept,
                enabled = !isResponding,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                if (isResponding) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Accept", fontSize = 13.sp, color = Color.White)
                }
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