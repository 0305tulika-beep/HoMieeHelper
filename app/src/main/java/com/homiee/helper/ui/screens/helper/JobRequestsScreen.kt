package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

private enum class RequestTab(val label: String) { ALL("All Requests"), NEW("New"), ACCEPTED("Accepted"), REJECTED("Rejected") }

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
    var selectedTab by remember { mutableStateOf(RequestTab.ALL) }

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
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(TealPrimary, TealPrimaryDark)))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Text("Job Requests", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Review and respond to new job requests from residents.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RequestTab.entries.forEach { tab ->
                    val count = when (tab) {
                        RequestTab.ALL -> HelperSampleData.allRequests.size
                        RequestTab.NEW -> HelperSampleData.newRequests.size
                        RequestTab.ACCEPTED -> HelperSampleData.acceptedRequests.size
                        RequestTab.REJECTED -> HelperSampleData.rejectedRequests.size
                    }
                    val selected = tab == selectedTab
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) TealPrimary else TealPale)
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tab.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else TealPrimaryDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) Color.White.copy(alpha = 0.25f) else Color.White)
                                .padding(horizontal = 7.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "$count",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else TealPrimary
                            )
                        }
                    }
                }
            }

            val list = when (selectedTab) {
                RequestTab.ALL -> HelperSampleData.allRequests
                RequestTab.NEW -> HelperSampleData.newRequests
                RequestTab.ACCEPTED -> HelperSampleData.acceptedRequests
                RequestTab.REJECTED -> HelperSampleData.rejectedRequests
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list, key = { it.id + it.status }) { request ->
                    JobRequestCard(
                        request = request,
                        onClick = { onOpenRequest(request.id) },
                        onAccept = { onAcceptRequest(request.id) },
                        onReject = { onRejectRequest(request.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
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
    SectionCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            ServiceIconBadge(icon = request.service.icon)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                StatusChip(
                    text = when (request.status) {
                        RequestStatus.NEW -> "New"
                        RequestStatus.ACCEPTED -> "Accepted"
                        RequestStatus.REJECTED -> "Rejected"
                    },
                    background = when (request.status) {
                        RequestStatus.NEW -> InfoCardBg
                        RequestStatus.ACCEPTED -> SuccessGreenBg
                        RequestStatus.REJECTED -> SosRedBg
                    },
                    textColor = when (request.status) {
                        RequestStatus.NEW -> TealPrimary
                        RequestStatus.ACCEPTED -> SuccessGreen
                        RequestStatus.REJECTED -> SosRed
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(request.service.label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(request.location, fontSize = 12.sp, color = TextSecondary)
            }
            Text(request.timeAgo, fontSize = 11.sp, color = HintGray)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoChip(text = request.date)
            InfoChip(text = request.time)
            InfoChip(text = request.duration)
        }

        if (request.status == RequestStatus.NEW) {
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