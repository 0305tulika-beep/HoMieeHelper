package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import com.homiee.helper.viewmodel.HelperBookingViewModel

/** Same subtle 1dp shadow used on Home / Job Requests / My Jobs / Profile cards. */
private val cardElevation = Modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = false)

@Composable
fun RequestDetailsScreen(
    requestId: String,
    viewModel: HelperBookingViewModel,
    onBackClick: () -> Unit
) {
    DashboardSystemBars(darkStatusBarIcons = true)

    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val respondingIds by viewModel.respondingIds.collectAsState()

    val bookingId = requestId.toIntOrNull()
    val request = remember(bookings, requestId) {
        bookings.find { it.id.toString() == requestId }?.toJobRequest()
    }
    val isResponding = bookingId != null && bookingId in respondingIds

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(
                title = "Request Details",
                onBackClick = onBackClick,
            )

            if (request == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isLoading) CircularProgressIndicator(color = TealPrimary)
                    else Text("Request not found", color = TextSecondary)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                SectionCard(modifier = cardElevation) {
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                        ServiceIconBadge(icon = request.service.icon, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(request.service.label, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailIconLine(text = request.date)
                            DetailIconLine(
                                text = if (request.duration.isBlank()) request.time
                                else "${request.time} (${request.duration})"
                            )
                            if (request.location.isNotBlank()) DetailIconLine(text = request.location)
                            if (request.salary.isNotBlank()) DetailIconLine(text = request.salary)
                        }
                        StatusChip(
                            text = when (request.status) {
                                RequestStatus.NEW -> "New Request"
                                RequestStatus.ACCEPTED -> "Confirmed"
                                RequestStatus.REJECTED -> "Rejected"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Resident Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                SectionCard(modifier = cardElevation) {
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
                SectionCard(modifier = cardElevation) {
                    InfoRow("Service Type", request.service.label)
                    InfoRow("Date", request.date)
                    InfoRow("Time", request.time)
                    if (request.duration.isNotBlank()) InfoRow("Duration", request.duration)
                    InfoRow("Expected Salary", request.salary, valueColor = TealPrimary)
                }

                if (request.instructions.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Special Instructions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoCard(text = "\u201C${request.instructions}\u201D", icon = Icons.Filled.FormatQuote)
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (request.status == RequestStatus.NEW && bookingId != null) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { viewModel.reject(bookingId) { ok -> if (ok) onBackClick() } },
                            enabled = !isResponding,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SosRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SosRed)
                        ) { Text("Reject Request", fontWeight = FontWeight.SemiBold) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { viewModel.accept(bookingId) { ok -> if (ok) onBackClick() } },
                            enabled = !isResponding,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            if (isResponding) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text("Accept Request", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
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