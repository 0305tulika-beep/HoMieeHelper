package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

@Composable
fun RequestDetailsScreen(
    requestId: String,
    onBackClick: () -> Unit,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit
) {
    DashboardSystemBars(darkStatusBarIcons = true)
    val request = HelperSampleData.requestById(requestId)

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(
                title = "Request Details",
                onBackClick = onBackClick,
                trailing = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = TextPrimary)
                    }
                }
            )

            if (request == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Request not found", color = TextSecondary)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                SectionCard {
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                        ServiceIconBadge(icon = request.service.icon, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(request.service.label, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailIconLine(text = "${request.date}")
                            DetailIconLine(text = "${request.time} (${request.duration})")
                            DetailIconLine(text = request.location)
                            DetailIconLine(text = request.salary)
                        }
                        StatusChip(text = "New Request")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Resident Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(initials = request.residentInitials, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(request.residentName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            if (request.email.isNotBlank()) Text(request.email, fontSize = 12.sp, color = TextSecondary)
                            if (request.phone.isNotBlank()) Text(request.phone, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    if (request.fullAddress.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(request.fullAddress, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Job Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                SectionCard {
                    InfoRow("Service Type", request.service.label)
                    InfoRow("Date", request.date)
                    InfoRow("Time", request.time)
                    InfoRow("Duration", request.duration)
                    InfoRow("Expected Salary", request.salary, valueColor = TealPrimary)
                }

                if (request.instructions.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Special Instructions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoCard(text = "\u201C${request.instructions}\u201D", icon = Icons.Filled.FormatQuote)
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (request.status == RequestStatus.NEW) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { onRejectRequest(request.id) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SosRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SosRed)
                        ) { Text("Reject Request", fontWeight = FontWeight.SemiBold) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { onAcceptRequest(request.id) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) { Text("Accept Request", color = Color.White, fontWeight = FontWeight.SemiBold) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailIconLine(text: String) {
    Text(text = text, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
}