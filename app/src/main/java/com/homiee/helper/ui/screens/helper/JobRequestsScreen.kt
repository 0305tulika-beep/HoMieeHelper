package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.R
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
            // Gradient header - background extends behind the status bar itself,
            // only the inner text content gets inset-padded, so there's no gap of
            // plain Scaffold background showing above it.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(TealPrimary, TealPrimaryDark)))
            ) {
                // Soft decorative icon on the right, like the reference.
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp)
                        .size(84.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(start = 20.dp, end = 110.dp, top = 18.dp, bottom = 18.dp)
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
                        Text(
                            text = loadError ?: "",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = { viewModel.load() }) {
                            Text("Retry", color = TealPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                requests.isEmpty() -> EmptyRequestsState()

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

/**
 * Empty state: illustration, title, description and a tip card, vertically
 * centered in the space under the header. Scrolls on very small screens.
 */
@Composable
private fun EmptyRequestsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(190.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "No new requests right now.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TealPrimaryDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You're all caught up! New job requests\nfrom residents will appear here.",
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Tip card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(TealPale.copy(alpha = 0.5f))
                .border(1.dp, TealPale, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TealPale),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Tip", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Check this section regularly so you don't miss any new requests.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
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
                border = BorderStroke(1.dp, SosRed),
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
        Text(text = text, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold.let { FontWeight.Medium })
    }
}